package com.example.workoutmate.domain.recommend.calculator;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.dto.UserActivityData;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.service.ZzimService;

import java.util.List;
import java.util.Map;

public interface ScoreCalculator {

    // 구현체 식별자 역할
    String getScoreType();

    Map<Long, Double> calculatorScores(
            User user,
            List<Board> boards,
            UserActivityData activityData,
            ParticipationService participationService,
            ZzimService zzimService
    );
}
