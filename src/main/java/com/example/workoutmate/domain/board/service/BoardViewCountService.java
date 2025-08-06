package com.example.workoutmate.domain.board.service;

import com.example.workoutmate.domain.board.dto.BoardResponseDto;
import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.BoardMapper;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  게시글 조회수를 hash에 저장해서 응답해주는 서비스
 */
@Service
@RequiredArgsConstructor
public class BoardViewCountService {

    @Autowired
    @Qualifier("customStringRedisTemplate")
    private RedisTemplate<String, String> stringRedisTemplate;
    private final BoardRepository boardRepository;

    public static final String VIEW_COUNT_HASH_KEY = "board:view:counts";

    @Transactional
    public void incrementViewCount(Long boardId) {
        // 캐시에 값이 없다면 DB값으로 세팅
        Object cached = stringRedisTemplate.opsForHash().get(VIEW_COUNT_HASH_KEY, boardId.toString());
        if (cached == null) {
            // DB에서 조회
            int dbViewCount = boardRepository.findById(boardId)
                    .map(Board::getViewCount)
                    .orElse(0);
            // Redis 해시에 조회수 값 저장
            stringRedisTemplate.opsForHash().put(VIEW_COUNT_HASH_KEY, boardId.toString(), String.valueOf(dbViewCount));
        }
        //  Redis 해시에서 해당 게시글의 조회수 값을 1 증가시킴
        stringRedisTemplate.opsForHash().increment(VIEW_COUNT_HASH_KEY, boardId.toString(), 1L);
    }

    // 해당 게시글의 조회수를 캐시에서 가져와서 dto에 뿌려줌
    public int getViewCount(Long boardId) {
        Object countObj = stringRedisTemplate.opsForHash().get(VIEW_COUNT_HASH_KEY, boardId.toString());

        return countObj == null ? 0 : Integer.parseInt(countObj.toString());
    }

    // 여러 게시글 페이지 변환
    public Page<BoardResponseDto> toDtoPage(Page<Board> boards) {
        // 게시글 ID 리스트 추출
        List<String> ids = boards.stream().map(b -> b.getId().toString()).toList();

        // Redis 해시에서 여러 게시글의 조회수를 한 번에 조회 (multiGet)
        List<Object> viewCounts = stringRedisTemplate.opsForHash().multiGet(VIEW_COUNT_HASH_KEY, new ArrayList<>(ids));

        List<BoardResponseDto> dtos = new ArrayList<>();
        int i = 0;
        // 각 게시글별로 조회수를 꺼내와서 DTO로 변환
        for (Board board : boards) {
            Object countObj = viewCounts.get(i);
            int viewCount = (countObj == null) ? 0 : Integer.parseInt(countObj.toString());
            dtos.add(BoardMapper.boardToBoardResponse(board, viewCount));
            i++;
        }
        return new PageImpl<>(dtos, boards.getPageable(), boards.getTotalElements());
    }

    // 2시간마다 Redis의 조회수 값을 DB로 백업
    @Scheduled(cron = "0 0 0/2 * * ?")// 2시간마다
    @Transactional
    public void syncViewCountToDB() {
        // Redis 해시 전체 (게시글별 조회수) 가져오기
        Map<Object, Object> allCounts = stringRedisTemplate.opsForHash().entries("board:view:counts");
        if (allCounts == null || allCounts.isEmpty()) return;

        // 동기화 대상 게시글 ID 리스트
        List<Long> boardIds = allCounts.keySet().stream()
                .map(key -> Long.valueOf(key.toString()))
                .toList();

        // DB에서 게시글 전체 한 번에 조회 (select 1회)
        List<Board> boards = boardRepository.findAllById(boardIds);

        Map<Long, Board> boardMap = boards.stream().collect(Collectors.toMap(Board::getId, b -> b));

        // 각 게시글의 Redis 값과 DB 값을 비교해 동기화
        for (Map.Entry<Object, Object> entry : allCounts.entrySet()) {
            Long boardId = Long.valueOf(entry.getKey().toString());
            int redisViewCount = Integer.parseInt(entry.getValue().toString());
            Board board = boardMap.get(boardId);

            // viewCount가 0이면(실제 DB가 0이 아니라면) update 하지 않음 (데이터 날아가는 것 방지)
            if (board != null && board.getViewCount() != redisViewCount && redisViewCount > 0) {
                // DB의 조회수와 Redis 값이 다를 때만 update 쿼리 실행 (불필요한 update 방지)
                boardRepository.updateViewCount(boardId, redisViewCount);
            }
        }
    }

    // 해시 에서 삭제
    public void removeFromHash(Long boardId) {
        stringRedisTemplate.opsForHash().delete(VIEW_COUNT_HASH_KEY, boardId.toString());

    }
}
