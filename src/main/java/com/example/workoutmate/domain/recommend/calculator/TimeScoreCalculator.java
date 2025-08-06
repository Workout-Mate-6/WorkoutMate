package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.dto.UserActivityData;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TimeScoreCalculator implements ScoreCalculator {
    // 인접 시간대
    private static final double ADJACENT_HOUR_WEIGHT = 0.3;

    @Override
    public String getScoreType() {
        return "time";
    }

    @Override
    public Map<Long, Double> calculatorScores(User user, List<Board> boards, UserActivityData activityData,
                                              ParticipationService participationService, ZzimService zzimService
    ) {
        List<Participation> participations = activityData.getParticipations();
        if (participations == null || participations.isEmpty()) {
            // 참여 기록 없으면 전부 0
            return boards.stream().collect(Collectors.toMap(Board::getId, b -> 0.0));
        }

        // 시간대 마다 참여 횟수 집계
        Map<Integer, Long> hourCount = calculateHourParticipation(participations);

        // 전체 참여 횟수
        long total = hourCount.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) {
            log.warn("유저 {}의 시간별 참여기록 없음", user.getId());
            return boards.stream().collect(Collectors.toMap(Board::getId, b -> 0.0));
        }
        log.debug("유저 {}의 시간별 참여 패턴 : {}", user.getId(), hourCount);

        // 게시글의 시간대에 맞는 점수 부여
        return boards.stream().collect(Collectors.toMap(Board::getId, board -> {
                    if (board.getStartTime() == null) {
                        log.warn("게시글 {}의 시작시간이 없음", board.getId());
                        return 0.0;
                    }
                    int boardHour = board.getStartTime().getHour();
                    double score = calculateTimeScore(boardHour, hourCount, total);
                    log.trace("게시글 : {}, 시간대 : {}, 시간점수 : {} ", board.getId(), boardHour, score);
                    return score;
                }
        ));
    }

    private Map<Integer, Long> calculateHourParticipation(List<Participation> participations) {
        return participations.stream()
                .filter(p -> p.getBoard() != null && p.getBoard().getStartTime() != null) // null 안전성 체크
                .map(p -> p.getBoard().getStartTime().getHour()) // 시간 추출
                .collect(Collectors.groupingBy(
                        Function.identity(), // 시간대별로 그룹핑
                        Collectors.counting() // 각 시간대별 참여 횟수 계산
                ));
    }

    private double calculateTimeScore(int boardHour, Map<Integer, Long> hourCount, long total) {
        // 해당 시간대 정확히 일치하는 참여 비율
        double exactScore = hourCount.getOrDefault(boardHour, 0L) / (double) total;

        // 인접 시간대 점수 계산 (24시간 순환 고려)
        int prevHour = (boardHour - 1 + 24) % 24; // 이전 시간 (23시 다음은 0시)
        int nextHour = (boardHour + 1) % 24;      // 다음 시간 (23시 다음은 0시)

        double prevScore = hourCount.getOrDefault(prevHour, 0L) / (double) total * ADJACENT_HOUR_WEIGHT;
        double nextScore = hourCount.getOrDefault(nextHour, 0L) / (double) total * ADJACENT_HOUR_WEIGHT;

        // 총점 계산 (정확한 시간 + 인접 시간들의 부분 점수)
        double totalScore = exactScore + prevScore + nextScore;

        // 최대 1.0으로 제한
        return Math.min(1.0, totalScore);
    }
}
