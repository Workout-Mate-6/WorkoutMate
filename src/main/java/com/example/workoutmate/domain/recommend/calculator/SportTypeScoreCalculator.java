package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.dto.UserActivityData;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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
            log.debug("{}유저의 참여기록 없으면 빵점설정", user.getId());
            return boards.stream().collect(Collectors.toMap(Board::getId, b -> 0.0));
        }

        // 타입별 선호도 계산
        Map<SportType, Double> typePreferences = calculatorTypePreferences(participations);

        log.debug("{}의 운동 타입 선호도: {}", user.getId(), typePreferences);

        // 게시글 운동 타입에 따라 점수 부여
        return boards.stream().collect(Collectors.toMap(Board::getId,
                board -> {
                    Double score = typePreferences.getOrDefault(board.getSportType(), 0.0);
                    log.trace("board {} ({}): 운동 타입 점수 = {}",
                            board.getId(), board.getSportType(), score);
                    return score;
                }

        ));
    }

    /**
     * 사용자의 운동 타입별 선호도 계산
     * 전체 참여 빈도와 30일 동안의 참여 반영
     * @param participations
     * @return
     */
    private Map<SportType, Double> calculatorTypePreferences(List<Participation> participations) {
        //전체 빈도 계산 (기존 SportTypeScoreCalculator 로직)
        Map<SportType, Long> typeFrequency = participations.stream()
                .collect(Collectors.groupingBy(p -> p.getBoard().getSportType(), Collectors.counting()));

        //최근(30일간) 활동 가중치 계산
        LocalDateTime cutoff = LocalDateTime.now().minusDays(RECENT_DAYS);
        Map<SportType, Double> recentTypeScore = participations.stream()
                .filter(p -> p.getCreatedAt().isAfter(cutoff))
                .collect(Collectors.groupingBy(
                        p -> p.getBoard().getSportType(),
                        Collectors.summingDouble(p -> RECENT_BOOST) // 최근 참여당 1.5점
                ));

        // 정규화 및 통합 점수 계산
        long maxFrequency = typeFrequency.values()
                .stream().mapToLong(Long::longValue).max().orElse(1);
        double maxRecentScore = recentTypeScore.values()
                .stream().mapToDouble(Double::doubleValue).max().orElse(1.0);

        // 최종 점수 계산
        return typeFrequency.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            SportType type = entry.getKey();
                            // 빈도 점수
                            double frequencyScore = entry.getValue() / (double) maxFrequency;
                            // 최근성 점수
                            double recentScore = recentTypeScore.getOrDefault(type, 0.0) / maxRecentScore;
                            return (frequencyScore + recentScore) / 2.0; // 두 점수의 평균
                        }
                ));
    }
}
