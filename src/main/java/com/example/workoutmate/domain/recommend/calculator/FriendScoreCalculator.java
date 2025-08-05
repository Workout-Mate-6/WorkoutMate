package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.dto.UserActivityData;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FriendScoreCalculator implements ScoreCalculator {
    @Override
    public String getScoreType() {
        return "friend";
    }

    @Override
    public Map<Long, Double> calculatorScores(User user, List<Board> boards, UserActivityData activityData, ParticipationService participationService, ZzimService zzimService) {
        Set<Long> friendIds = activityData.getFriendIds();
        Map<Long, Set<Long>> boardParticipants = participationService.getBoardParticipants(boards);

        return boards.stream().collect(Collectors.toMap(Board::getId, board -> {
                    Set<Long> participants = boardParticipants.getOrDefault(board.getId(), Collections.emptySet());
                    for (Long friendId : friendIds) {
                        if (participants.contains(friendId)) {
                            return 1.0;
                        }
                    }
                    return 0.0;
                }
        ));
    }
}
