package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
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
public class TypeScoreCalculator implements ScoreCalculator {
    @Override
    public String getScoreType() {
        return "sportType";
    }

    @Override
    public Map<Long, Double> calculatorScores(User user, List<Board> boards,
                                              UserActivityData activityData, ParticipationService participationService,
                                              ZzimService zzimService
    ) {
        List<Participation> participations = activityData.getParticipations();
        if (participations == null || participations.isEmpty()) {
            // 참여 기록 없으면 전부 0점
            return boards.stream().collect(Collectors.toMap(Board::getId, b -> 0.0));
        }

        // 운동 타입마다 참여횟수 확인
        Map<SportType, Long> typeCount = participations.stream()
                .collect(Collectors.groupingBy(p -> p.getBoard().getSportType(), Collectors.counting()));

        // 전체 참여 횟수
        long total = typeCount.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) {
            return boards.stream().collect(Collectors.toMap(Board::getId, b -> 0.0));
        }
        return boards.stream().collect(Collectors.toMap(Board::getId, board -> {
                    long count = typeCount.getOrDefault(board.getSportType(), 0L);
                    return (double) count / total;
                }
        ));
    }
}
