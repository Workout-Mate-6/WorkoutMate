package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
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
public class TypeScoreCalculator implements ScoreCalculator{
    @Override
    public String getScoreType() {
        return "sportType";
    }

    @Override
    public Map<Long, Double> calculatorScores(User user, List<Board> boards,
                                              UserActivityData activityData, ParticipationService participationService,
                                              ZzimService zzimService
    ) {
        Set<SportType> preferredTypes = activityData.getPreferredSportTypes();

        return boards.stream().collect(Collectors.toMap(Board::getId,
                board -> preferredTypes.contains(board.getSportType()) ? 1.0 : 0.0
        ));
    }
}
