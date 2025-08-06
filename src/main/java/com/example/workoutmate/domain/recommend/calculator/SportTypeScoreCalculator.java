package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.dto.UserActivityData;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SportTypeScoreCalculator implements ScoreCalculator {
    private static final double RECENT_BOOST = 1.5;
    private static final int RECENT_DAYS = 30;
    @Override
    public String getScoreType() {
        return "sportType";
    }

    @Override
    public Map<Long, Double> calculatorScores(User user, List<Board> boards,
                                              UserActivityData activityData,
                                              ParticipationService participationService,
                                              ZzimService zzimService
    ) {
        List<Participation> participations = activityData.getParticipations();
        if (participations == null || participations.isEmpty()) {
            // 참여 기록 없으면 전부 0점
            return boards.stream().collect(Collectors.toMap(Board::getId, b -> 0.0));
        }

        // 운동 타입마다 참여횟수 확인
        Map<SportType, Double> typePreferences = calculatorTypePreferences(participations);

        return boards.stream().collect(Collectors.toMap(Board::getId,
                board -> typePreferences.getOrDefault(board.getSportType(), 0.0)));
    }

    private Map<SportType, Double> calculatorTypePreferences(List<Participation> participations) {
        // 1. 전체 빈도 계산 (기존 SportTypeScoreCalculator 로직)
        Map<SportType, Long> typeFrequency = participations.stream()
                .collect(Collectors.groupingBy(p -> p.getBoard().getSportType(), Collectors.counting()));

        // 2. 최근 활동 가중치 계산 (기존 ParticipationScoreCalculator 로직)
        LocalDateTime cutoff = LocalDateTime.now().minusDays(RECENT_DAYS);
        Map<SportType, Double> recentTypeScore = participations.stream()
                .filter(p -> p.getCreatedAt().isAfter(cutoff))
                .collect(Collectors.groupingBy(
                        p -> p.getBoard().getSportType(),
                        Collectors.summingDouble(p -> RECENT_BOOST)
                ));

        // 3. 정규화 및 통합 점수 계산
        long maxFrequency = typeFrequency.values()
                .stream().mapToLong(Long::longValue).max().orElse(1);
        double maxRecentScore = recentTypeScore.values()
                .stream().mapToDouble(Double::doubleValue).max().orElse(1.0);

        return typeFrequency.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            SportType type = entry.getKey();
                            double frequencyScore = entry.getValue() / (double) maxFrequency;
                            double recentScore = recentTypeScore.getOrDefault(type, 0.0) / maxRecentScore;
                            return (frequencyScore + recentScore) / 2.0; // 두 점수의 평균
                        }
                ));
    }
}
