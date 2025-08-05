package com.example.workoutmate.domain.recommend.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.service.BoardService;
import com.example.workoutmate.domain.follow.service.FollowService;
import com.example.workoutmate.domain.participation.service.ParticipationCreateService;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.calculator.ScoreCalculator;
import com.example.workoutmate.domain.recommend.config.RecommendationConfig;
import com.example.workoutmate.domain.recommend.dto.BoardResponseDto;
import com.example.workoutmate.domain.recommend.dto.RecommendationDto;
import com.example.workoutmate.domain.recommend.dto.UserActivityData;
import com.example.workoutmate.domain.recommend.dto.UserSimpleDto;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final BoardService boardService;
    private final UserService userService;
    private final ParticipationService participationService;
    private final ZzimService zzimService;
    private final FollowService followService;
    private final RecommendationConfig recommendationConfig;

    private final List<ScoreCalculator> scoreCalculators;

    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendations(Long userId, int limit) {
        User user = userService.findById(userId);
        Pageable pageable = PageRequest.of(0, recommendationConfig.getMaxCandidateSize());
        List<Board> candidateBoards = boardService.findRecommendationCandidates(userId, pageable);

        if (candidateBoards.isEmpty()) {
            return Collections.emptyList();
        }
        UserActivityData activityData = UserActivityData.builder()
                .participations(participationService.findByApplicant_Id(userId))
                .zzims(zzimService.findByUserId(userId)).friends(followService.findByFollower_Id(userId)).build();

        // 점수 계산
        Map<String, Map<Long, Double>> scoreByType = scoreCalculators.stream().collect(
                Collectors.toMap(ScoreCalculator::getScoreType,
                        calc -> calc.calculatorScores(
                                user, candidateBoards, activityData, participationService, zzimService)
                )
        );

        // 가중치 반영!
        return candidateBoards.stream().map(board -> {
                    long boardId = board.getId();
                    double participationScore = scoreByType.getOrDefault("participation", Map.of()).getOrDefault(boardId, 0.0);
                    double zzimScore = scoreByType.getOrDefault("zzim", Map.of()).getOrDefault(boardId, 0.0);
                    double friendScore = scoreByType.getOrDefault("friend", Map.of()).getOrDefault(boardId, 0.0);
                    double typeScore = scoreByType.getOrDefault("type", Map.of()).getOrDefault(boardId, 0.0);
                    double timeScore = scoreByType.getOrDefault("time", Map.of()).getOrDefault(boardId, 0.0);

                    double finalScore =
                            participationScore * recommendationConfig.getWeights().getParticipation()
                                    + zzimScore * recommendationConfig.getWeights().getZzim()
                                    + friendScore * recommendationConfig.getWeights().getFriend()
                                    + typeScore * recommendationConfig.getWeights().getType()
                                    + timeScore * recommendationConfig.getWeights().getTime();
                    return RecommendationDto.builder()
                            .board(BoardResponseDto.builder()
                                    .id(board.getId())
                                    .title(board.getTitle())
                                    .content(board.getContent())
                                    .maxParticipants(board.getMaxParticipants())
                                    .currentParticipants(board.getCurrentParticipants())
                                    .writer(UserSimpleDto.builder()
                                            .id(user.getId())
                                            .nickname(user.getName())
                                            .build())
                                    .build())
                            .finalScore(finalScore)
                            .participationScore(participationScore)
                            .zzimScore(zzimScore)
                            .friendScore(friendScore)
                            .typeScore(typeScore)
                            .timeScore(timeScore)
                            .build();
                })
                .filter(dto -> dto.getFinalScore() > recommendationConfig.getMinScore())
                .sorted(Comparator.comparingDouble(RecommendationDto::getFinalScore).reversed()).limit(limit)
                .collect(Collectors.toList());

    }
}
