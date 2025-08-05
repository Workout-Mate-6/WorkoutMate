package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
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
        // 유저가 자주 참여하는 ㅅ ㅣ간대와 모집 시간대가 일치하면 1 아니면 0
        Set<Integer> preferredHours = activityData.getPreferredHours();

        return boards.stream().collect(Collectors.toMap(Board::getId,
                board -> preferredHours.contains(board.getCreatedAt().getHour()) ? 1.0 : 0.0));
    }
}
