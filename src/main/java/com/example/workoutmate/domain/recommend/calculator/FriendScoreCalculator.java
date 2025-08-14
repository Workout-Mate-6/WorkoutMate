package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.dto.UserActivityData;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FriendScoreCalculator implements ScoreCalculator {
    private static final double FRIEND_SCORE_WEIGHT = 0.3;

    @Override
    public String getScoreType() {
        return "friend";
    }

    @Override
    public Map<Long, Double> calculatorScores(User user, List<Board> boards, UserActivityData activityData, ParticipationService participationService, ZzimService zzimService) {

        // 캐싱된 친구 목록 사용
        Set<Long> friendIds = activityData.getFriendIds();

        // 친구가 없으면 모든 게시글에 0점
        if (friendIds.isEmpty()) {
            log.debug("{}에게 친구 없으면 점수 빵점", user.getId());
            return boards.stream().collect(Collectors.toMap(Board::getId, b -> 0.0));
        }
        log.debug("{}의 친구{}명에 대한 친구 계산", user.getId(), friendIds.size());

        //게시글 별로 참여중인 친구 수에 따른 점수 계산
        Map<Long, Set<Long>> boardParticipants = participationService.getBoardParticipants(boards);

        return boards.stream().collect(Collectors.toMap(Board::getId, board -> {
                    Set<Long> participants = boardParticipants.getOrDefault(board.getId(), Collections.emptySet());

                    //친구 수 계산
                    long friendCount = participants.stream().filter(friendIds::contains).count();

                    // 친구수 비례로 점수 부과 (인당 0.3 최대 1점)
                    double score = Math.min(1.0, friendCount * FRIEND_SCORE_WEIGHT);
                    if (friendCount > 0) {
                        log.trace("{}에 친구 {}명 참여중 친구 점수 : {}", board.getId(), friendCount, score);
                    }
                    return score;
                }
        ));
    }
}
