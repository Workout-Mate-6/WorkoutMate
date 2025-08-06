package com.example.workoutmate.domain.recommend.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.board.service.BoardService;
import com.example.workoutmate.domain.follow.service.FollowService;
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
        Pageable pageable = PageRequest.of(0, recommendationConfig.getMaxCandidateSize()); // 최대 1000개까지만 후보 게시글을 가져옴(1000개인 이유는 야물에서 1000개 까지만 설정했기 때문)
        List<Board> candidateBoards = boardService.findRecommendationCandidates(userId, pageable);

        if (candidateBoards.isEmpty()) {
            return Collections.emptyList();
        }
        UserActivityData activityData = UserActivityData.builder()
                .participations(participationService.findByApplicant_Id(userId))
                .zzims(zzimService.findByUserId(userId)).friends(followService.findByFollower_Id(userId)).build();

        Set<SportType> preferredTypes = activityData.getPreferredSportTypes();
        Set<Integer> preferredHours = activityData.getPreferredHours();

        for (Board board : candidateBoards) {
            System.out.println("boardId: " + board.getId() +
                    ", boardSportType: " + board.getSportType() +
                    ", preferredTypes: " + preferredTypes +
                    ", 포함여부: " + preferredTypes.contains(board.getSportType()));
        }

        // 제대로 찍히는지 검증
        // 여기서 participation, 선호 타입/시간 set 직접 println!
        System.out.println("=== 참여 내역: " + activityData.getParticipations());
        System.out.println("=== 선호 운동 타입: " + activityData.getPreferredSportTypes());
        System.out.println("=== 선호 시간대: " + activityData.getPreferredHours());

        // 점수 계산
        Map<String, Map<Long, Double>> scoreByType = scoreCalculators.stream().collect(
                Collectors.toMap(ScoreCalculator::getScoreType,
                        calc -> {
                            Map<Long, Double> scores = calc.calculatorScores(
                                    user, candidateBoards, activityData, participationService, zzimService);
                            return scores;
                        }
                )
        );

        List<RecommendationDto> recommendations = candidateBoards.stream()
                .map(board -> createrecommendationDto(board, scoreByType))
                .filter(dto ->dto.getFinalScore() > recommendationConfig.getMinScore())
                .sorted(Comparator.comparingDouble(RecommendationDto::getFinalScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        return recommendations;


    }

    private RecommendationDto createrecommendationDto(Board board, Map<String, Map<Long, Double>> scoreByType) {
        long boardId = board.getId();
        RecommendationConfig.Weights weights = recommendationConfig.getWeights();

        // 각 점수 타입별 점수 추출
        double participationScore = scoreByType.getOrDefault("participation", Map.of()).getOrDefault(boardId, 0.0);
        double zzimScore = scoreByType.getOrDefault("zzim", Map.of()).getOrDefault(boardId, 0.0);
        double friendScore = scoreByType.getOrDefault("friend", Map.of()).getOrDefault(boardId, 0.0);
        double sportTypeScore = scoreByType.getOrDefault("sportType", Map.of()).getOrDefault(boardId, 0.0);
        double timeScore = scoreByType.getOrDefault("time", Map.of()).getOrDefault(boardId, 0.0);

        // 가중치 적용하여 최종 점수 계산
        double finalScore = participationScore * weights.getParticipation()
                + zzimScore * weights.getZzim()
                + friendScore * weights.getFriend()
                + sportTypeScore * weights.getType()
                + timeScore * weights.getTime();

        return RecommendationDto.builder()
                .board(BoardResponseDto.builder()
                        .id(board.getId())
                        .title(board.getTitle())
                        .content(board.getContent())
                        .maxParticipants(board.getMaxParticipants())
                        .currentParticipants(board.getCurrentParticipants())
                        .writer(UserSimpleDto.builder()
                                .id(board.getWriter().getId()) // 수정: board의 작성자 정보 사용
                                .nickname(board.getWriter().getName())
                                .build())
                        .build())
                .finalScore(finalScore)
                .participationScore(participationScore)
                .zzimScore(zzimScore)
                .friendScore(friendScore)
                .typeScore(sportTypeScore) // sportType 점수로 통일
                .timeScore(timeScore)
                .build();
    }
}
