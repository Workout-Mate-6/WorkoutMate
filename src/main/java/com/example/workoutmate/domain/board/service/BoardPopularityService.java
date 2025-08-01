package com.example.workoutmate.domain.board.service;

import com.example.workoutmate.domain.board.dto.PopularBoardDto;
import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.BoardMapper;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardPopularityService {

    @Autowired
    @Qualifier("customStringRedisTemplate")
    private RedisTemplate<String, String> stringRedisTemplate;
    private final BoardRepository boardRepository;
    private final ObjectMapper objectMapper;


    private static final String VIEW_RANKING_KEY = "board:view:ranking"; // Redis에서 인기글 랭킹을 저장하는 키 (zset에 저장)
    private static final String POPULAR_TOP10_KEY = "board:popular:top10"; // 인기글 top10 리스트를 저장할 캐시 키

    // 해당 게시글 조회수 증가 (ZSet)
    @Transactional
    public void incrementViewCount(Long boardId) {
        stringRedisTemplate.opsForZSet().incrementScore(VIEW_RANKING_KEY, boardId.toString(), 1.0);
    }

    // 인기글 Top10을 캐시에 저장
    @Scheduled(fixedRate = 60 * 1000)
    @Transactional(readOnly = true)
    public void cacheTop10BoardDetails() {
        // Zset에서 상위 10개의 boardId를 랭킹 순으로 추출
        Set<String> boardId = stringRedisTemplate.opsForZSet().reverseRange(VIEW_RANKING_KEY, 0, 9);

        if (boardId == null || boardId.isEmpty()) {
            stringRedisTemplate.delete(POPULAR_TOP10_KEY);
            return;
        }

        // boardId를 String -> Long 으로 변환
        List<Long> boardIds = boardId.stream().map(Long::valueOf).toList();

        // DB에서 boardId 목록에 해당하는 Board 엔티티 조회
        List<Board> boards = boardRepository.findAllById(boardIds);

        Map<Long, Board> boardMap = boards.stream().collect(Collectors.toMap(Board::getId, b -> b));

        // Top10 id 순서대로 PopularBoardDto로 변환
        List<PopularBoardDto> result = boardIds.stream()
                .map(id -> {
                    Board board = boardMap.get(id);

                    if (board == null) return null;

                    // Redis ZSet에서 score(조회수) 읽기
                    Double score = stringRedisTemplate.opsForZSet().score(VIEW_RANKING_KEY, id.toString());

                    return BoardMapper.toPopularBoardDto(board, score != null ? score.intValue() : 0);
                })
                .filter(Objects::nonNull)
                .toList();

        // 인기글 리스트를 JSON으로 직렬화해서 Redis에 저장
        try {
            String json = objectMapper.writeValueAsString(result);
            stringRedisTemplate.opsForValue().set(POPULAR_TOP10_KEY, json, Duration.ofMinutes(10));
        } catch (Exception e) {
            // 예외처리, 로깅 등
        }
    }

    // 캐시만 읽어서 반환
    @Transactional(readOnly = true)
    public List<PopularBoardDto> getPopularBoardsFromCache() {
        try {
            // 캐시에 저장된 인기글 Top10 리스트(JSON)를 꺼내옴
            String json = stringRedisTemplate.opsForValue().get(POPULAR_TOP10_KEY);

            if (json != null) {
                // JSON 문자열을 PopularBoardDto 리스트로 파싱
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, PopularBoardDto.class);
                return objectMapper.readValue(json, type);
            }
        } catch (Exception e) {
            log.warn("Redis에서 인기글 조회 실패, DB에서 조회합니다", e);
        }
        // 캐시 미스 or 에러 시 DB에서 Top10 반환
        return getPopularBoardsFromDatabase();
    }

    // db에서 인기글 top10 조회 (Fallback, 캐시 미스 시 사용)
    @Transactional(readOnly = true)
    public List<PopularBoardDto> getPopularBoardsFromDatabase() {
        return boardRepository.findTop10ByOrderByViewCountDesc()
                .stream()
                .map(board -> BoardMapper.toPopularBoardDto(board, board.getViewCount()))
                .toList();
    }

    // redis -> db로 viewCount 저장
    @Scheduled(cron = "0 0 * * * ?") // 1시간마다
    @Transactional
    public void syncViewCountToDB() {
        Set<String> ids = stringRedisTemplate.opsForZSet().range(VIEW_RANKING_KEY, 0, -1);
        if (ids == null || ids.isEmpty()) return;

        for (String id : ids) {
            Double score = stringRedisTemplate.opsForZSet().score(VIEW_RANKING_KEY, id);
            if (score != null) {
                boardRepository.findById(Long.valueOf(id))
                        .ifPresent(board -> {
                            board.increaseViewCount(score.intValue());
                            boardRepository.save(board);
                        });
            }
        }
    }
}
