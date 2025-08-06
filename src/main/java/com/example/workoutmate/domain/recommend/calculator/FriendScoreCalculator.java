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
    private static final double FRIEND_SCORE_WEIGHT = 0.3;

    @Override
    public String getScoreType() {
        return "friend";
    }

    @Override
    public Map<Long, Double> calculatorScores(User user, List<Board> boards, UserActivityData activityData, ParticipationService participationService, ZzimService zzimService) {
        Set<Long> friendIds = activityData.getFriendIds();
        // 친구가 없으면 모든 게시글에 0점
        if (friendIds.isEmpty()) {
            return boards.stream().collect(Collectors.toMap(Board::getId, b -> 0.0));
        }

        Map<Long, Set<Long>> boardParticipants = participationService.getBoardParticipants(boards);

        return boards.stream().collect(Collectors.toMap(Board::getId, board -> {
                    Set<Long> participants = boardParticipants.getOrDefault(board.getId(), Collections.emptySet());
                    long friendCount = participants.stream().filter(friendIds::contains).count();
                    double score = Math.min(1.0, friendCount * FRIEND_SCORE_WEIGHT);

                    return score;
                }
        ));
    }
}
