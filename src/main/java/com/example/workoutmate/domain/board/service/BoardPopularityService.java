package com.example.workoutmate.domain.board.service;

import com.example.workoutmate.domain.board.dto.PopularBoardDto;
import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.BoardMapper;
import com.example.workoutmate.domain.board.enums.Status;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static final String WEEKLY_VEIW_RANKING_KEY = "board:view:ranking:weekly:";
    public static final String DAILY_VIEW_RANKING_KEY = "board:view:ranking"; // Redis에서 인기글 랭킹을 저장하는 키 (날짜별 ZSet)
    public static final String POPULAR_TOP10_KEY = "board:popular:top10"; // 인기글 top10 리스트를 저장할 캐시 키

    // 날짜별 ZSet Key
    private String getDailyRankingKey(LocalDate dateTime) {
        return DAILY_VIEW_RANKING_KEY + ":" + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); // 에시 : board:view:ranking:2025-08-07
    }

    // 해당 게시글 조회수 증가 (ZSet)
    @Transactional
    public void incrementViewCount(Long boardId) {
        String todayKey = getDailyRankingKey(LocalDate.now());
        stringRedisTemplate.opsForZSet().incrementScore(todayKey, boardId.toString(), 1.0);
        stringRedisTemplate.expire(todayKey, Duration.ofDays(8));
    }

    // 최근 7일치 ZSet을 unionAndStore로 합산해서 Top10 캐시
    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional(readOnly = true)
    public void cacheTop10BoardDetails() {
        // 최근 7일 key 저장
        List<String> zsetKeys = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            zsetKeys.add(getDailyRankingKey(date));
        }
        Collections.reverse(zsetKeys);

        String unionKey = WEEKLY_VEIW_RANKING_KEY + LocalDate.now();

        // 7일치 ZSet을 unionAndStore로 합산
        if (zsetKeys.size() >= 2) {
            stringRedisTemplate.opsForZSet().unionAndStore(zsetKeys.get(0), zsetKeys.subList(1, zsetKeys.size()), unionKey);
            stringRedisTemplate.expire(unionKey, Duration.ofDays(8));
        } else if (zsetKeys.size() == 1) {
            stringRedisTemplate.opsForZSet().unionAndStore(zsetKeys.get(0), Collections.emptyList(), unionKey);
            stringRedisTemplate.expire(unionKey, Duration.ofDays(8));
        }

        // unionKey에서 Top10을 뽑기
        Set<String> boardId = stringRedisTemplate.opsForZSet().reverseRange(unionKey, 0, 9);

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
                    Double score = stringRedisTemplate.opsForZSet().score(unionKey, id.toString());

                    // 조회수 0 이상만 포함
                    if (score == null || score == 0) return null;
                    return BoardMapper.toPopularBoardDto(board, score.intValue());
                })
                .filter(Objects::nonNull)
                .toList();

        // 인기글 리스트를 JSON으로 직렬화해서 Redis에 저장
        try {
            String json = objectMapper.writeValueAsString(result);
            stringRedisTemplate.opsForValue().set(POPULAR_TOP10_KEY, json, Duration.ofMinutes(65));
        } catch (Exception e) {
            log.error("인기글 리스트 캐싱 중 예외 발생", e);
        }
    }

    // 캐시만 읽어서 반환
    @Transactional(readOnly = true)
    public List<PopularBoardDto> getPopularBoardsFromCache() {
        try {
            // 캐시에 저장된 인기글 Top10 리스트(JSON)를 꺼내옴
            String json = stringRedisTemplate.opsForValue().get(POPULAR_TOP10_KEY);

            if (json != null) {
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, PopularBoardDto.class);
                return objectMapper.readValue(json, type);
            }
        } catch (Exception e) {
            log.warn("Redis에서 인기글 조회 실패", e);
        }

        return List.of();
    }

    // 랭킹에서 삭제
    public void removeFromRanking(Long boardId) {
        for (int i = 0; i < 7; i++) {
            LocalDateTime date = LocalDateTime.now().minusMinutes(i);
            String key = "board:view:ranking:" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            stringRedisTemplate.opsForZSet().remove(key, boardId.toString());
        }
        // unionKey(weekly)도 삭제
        String unionKey = WEEKLY_VEIW_RANKING_KEY + LocalDate.now();
        stringRedisTemplate.opsForZSet().remove(unionKey, boardId.toString());
        // 캐시 삭제
        stringRedisTemplate.delete(POPULAR_TOP10_KEY);
    }
}
