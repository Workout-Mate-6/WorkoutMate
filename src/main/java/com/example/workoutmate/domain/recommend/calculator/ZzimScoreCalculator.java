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
public class ZzimScoreCalculator implements ScoreCalculator{
    @Override
    public String getScoreType() {
        return "zzim";
    }

    @Override
    public Map<Long, Double> calculatorScores(User user, List<Board> boards, UserActivityData activityData, ParticipationService participationService, ZzimService zzimService) {
        // 유저가 찜한거는 1.0 안한거는 0.0
        Set<Long> zzimBordId = zzimService.getZzimBoardIdByUser(user);

        return boards.stream().collect(Collectors.toMap(
                Board::getId,
                board -> zzimBordId.contains(board.getId()) ? 1.0 : 0.0
        ));
    }
}
