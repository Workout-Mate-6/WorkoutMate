package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.dto.UserActivityData;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ZzimScoreCalculator implements ScoreCalculator{
    @Override
    public String getScoreType() {
        return "zzim";
    }

    @Override
    public Map<Long, Double> calculatorScores(User user, List<Board> boards, UserActivityData activityData, ParticipationService participationService, ZzimService zzimService) {

        Set<Long> zzimBordId = activityData.getZzimBoardIds();
        log.debug("유저 {}이 찜한 게시글에 {}개에 대한 점수 계산", user.getId(),zzimBordId.size());
        return boards.stream().collect(Collectors.toMap(
                Board::getId,
                board -> {
                    double score = zzimBordId.contains(board.getId()) ? 1.0 : 0.0;
                    if (score > 0) {
                        log.trace("유저 {}가 게시글 {}를 찜했음, 점수 추가", user.getId(), board.getId());
                    }
                    return score;
                }
        ));
    }
}
