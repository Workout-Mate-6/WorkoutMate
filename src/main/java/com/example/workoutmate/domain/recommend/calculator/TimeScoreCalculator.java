package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.dto.UserActivityData;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TimeScoreCalculator implements ScoreCalculator {
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
        Map<Integer, Long> hourCount = participations.stream()
                .map(p -> p.getBoard().getStartTime().getHour())
                .collect(Collectors.groupingBy(hour -> hour, Collectors.counting()));

        // 전체 참여 횟수
        long total = hourCount.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) {
            return boards.stream().collect(Collectors.toMap(Board::getId, b -> 0.0));
        }

        // 게시글의 시간대에 맞는 점수 부여
        return boards.stream().collect(Collectors.toMap(Board::getId, board -> {
                    int hour = board.getStartTime().getHour();
                    long count = hourCount.getOrDefault(hour, 0L);
                    return (double) count / total;
                }
        ));
    }
}
