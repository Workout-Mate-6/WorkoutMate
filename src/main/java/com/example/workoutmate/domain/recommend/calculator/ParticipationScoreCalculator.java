package com.example.workoutmate.domain.recommend.calculator;


import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.dto.UserActivityData;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ParticipationScoreCalculator implements ScoreCalculator{
    @Override
    public String getScoreType() {
        return "participation";
    }

    // 게시글의 운동 타입이, 해당 유저가 얼마나 좋아하는 타입인지 비율 점수로 환산
    @Override
    public Map<Long, Double> calculatorScores(User user, List<Board> boards, UserActivityData activityData,
                                              ParticipationService participationService, ZzimService zzimService) {
        if (activityData.getParticipations() == null || activityData.getParticipations().isEmpty()) {
            return boards.stream().collect(Collectors.toMap(Board::getId, b ->0.0));
        }

        Map<String, Double> typePreferences = calculateTypePreferences(activityData.getParticipations());

        return boards.stream().collect(Collectors.toMap(
                Board::getId,
                board -> typePreferences.getOrDefault(board.getSportType().name(), 0.0)
        ));
    }

    private Map<String, Double> calculateTypePreferences(List<Participation> participations) {
        Map<String, Long> typeFrequency = participations.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getBoard().getSportType().name(),
                        Collectors.counting()
                ));
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Map<String, Double> recentTypeScore = participations.stream()
                .filter(p->p.getCreatedAt().isAfter(thirtyDaysAgo)) // 30일간의 participation만 필터
                .collect(Collectors.groupingBy(
                        p->p.getBoard().getSportType().name(), // 운동 타입마다
                        Collectors.summingDouble(p->1.5))); // 참여 건당 1.5점 씩

        long maxFrequency = typeFrequency.values().stream().mapToLong(x->x).max().orElse(1);
        double maxRecentScore = recentTypeScore.values().stream().mapToDouble(x->x).max().orElse(1.0);

        return typeFrequency.keySet().stream().collect(Collectors.toMap(Function.identity(),
                type -> {
                    double frequencyScore = (double) typeFrequency.get(type) / maxFrequency;
                    double recentScore = recentTypeScore.getOrDefault(type, 0.0) / maxRecentScore;
                    return (frequencyScore + recentScore) / 2.0;
                }
        ));
    }




}
