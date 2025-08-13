//package com.example.workoutmate.domain.recommend.service;
//
//import com.example.workoutmate.domain.board.entity.Board;
//import com.example.workoutmate.domain.board.entity.SportType;
//import com.example.workoutmate.domain.board.service.BoardService;
//import com.example.workoutmate.domain.follow.service.FollowService;
//import com.example.workoutmate.domain.participation.service.ParticipationService;
//import com.example.workoutmate.domain.recommend.calculator.ScoreCalculator;
//import com.example.workoutmate.domain.recommend.config.RecommendationConfig;
//import com.example.workoutmate.domain.recommend.dto.BoardResponseDto;
//import com.example.workoutmate.domain.recommend.dto.RecommendationDto;
//import com.example.workoutmate.domain.recommend.dto.UserActivityData;
//import com.example.workoutmate.domain.recommend.dto.UserSimpleDto;
//import com.example.workoutmate.domain.user.entity.User;
//import com.example.workoutmate.domain.user.service.UserService;
//import com.example.workoutmate.domain.zzim.service.ZzimService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class RecommendationService {
//
//    private final BoardService boardService;
//    private final UserService userService;
//    private final ParticipationService participationService;
//    private final ZzimService zzimService;
//    private final FollowService followService;
//    private final RecommendationConfig recommendationConfig;
//
//    private final List<ScoreCalculator> scoreCalculators;
//
//    @Transactional(readOnly = true)
//    public List<RecommendationDto> getRecommendations(Long userId, int limit) {
//        User user = userService.findById(userId);
//        Pageable pageable = PageRequest.of(0, recommendationConfig.getMaxCandidateSize()); // 최대 1000개까지만 후보 게시글을 가져옴(1000개인 이유는 야물에서 1000개 까지만 설정했기 때문)
//        List<Board> candidateBoards = boardService.findRecommendationCandidates(userId, pageable);
//
//        if (candidateBoards.isEmpty()) {
//            return Collections.emptyList();
//        }
//        UserActivityData activityData = UserActivityData.builder()
//                .participations(participationService.findByApplicant_Id(userId))
//                .zzims(zzimService.findByUserId(userId)).friends(followService.findByFollower_Id(userId)).build();
//
//        // 로깅에 사용
//        Set<SportType> preferredTypes = activityData.getPreferredSportTypes();
//        Set<Integer> preferredHours = activityData.getPreferredHours();
//
//        log.debug("유저 {}의 활동 기록 = 참여 : {}회, 찜 : {}개, 친구 : {}",
//                userId,
//                activityData.getParticipations().size(),
//                activityData.getZzims().size(),
//                activityData.getFriends().size());
//
//        log.debug("유저 {}의 선호 패턴 - 운동타입: {}, 시간대: {}",
//                userId, preferredTypes, preferredHours);
//
//        // 점수 계산
//        Map<String, Map<Long, Double>> scoreByType = scoreCalculators.stream().collect(
//                Collectors.toMap(ScoreCalculator::getScoreType,
//                        calc -> {
//                            String scoreType = calc.getScoreType();
//                            Map<Long, Double> scores = calc.calculatorScores(
//                                    user, candidateBoards, activityData, participationService, zzimService);
//                            log.debug("{} 점수 계산 완료 - 평균: {}",
//                                    scoreType,
//                                    scores.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
//                            );
//                            return scores;
//                        }
//                )
//        );
//
//        // 추천 결과 생성
//        List<RecommendationDto> recommendations = candidateBoards.stream()
//                .map(board -> createrecommendationDto(board, scoreByType))
//                .filter(dto -> {
//                    boolean passesThreshold = dto.getFinalScore() > recommendationConfig.getMinScore();
//                    if (!passesThreshold) {
//                        log.trace("Board {} 최소 점수 미달로 제외 (점수: {})",
//                                dto.getBoard().getId(), dto.getFinalScore());
//                    }
//                    return passesThreshold;
//                })
//                .sorted(Comparator.comparingDouble(RecommendationDto::getFinalScore).reversed())
//                .limit(limit)
//                .collect(Collectors.toList());
//        log.info("사용자 {} 추천 완료 - {}개 반환 (후보 {}개 중)",
//                userId, recommendations.size(), candidateBoards.size());
//        return recommendations;
//
//
//    }
//
//    /**
//     * 게시글별 종합 점수 게산 및 추천 DTO
//     * @param board
//     * @param scoreByType
//     * @return
//     */
//    private RecommendationDto createrecommendationDto(Board board, Map<String, Map<Long, Double>> scoreByType) {
//        long boardId = board.getId();
//        RecommendationConfig.Weights weights = recommendationConfig.getWeights();
//
//        // 각 점수 타입별 점수 추출
//        double participationScore = scoreByType.getOrDefault("participation", Map.of()).getOrDefault(boardId, 0.0);
//        double zzimScore = scoreByType.getOrDefault("zzim", Map.of()).getOrDefault(boardId, 0.0);
//        double friendScore = scoreByType.getOrDefault("friend", Map.of()).getOrDefault(boardId, 0.0);
//        double sportTypeScore = scoreByType.getOrDefault("sportType", Map.of()).getOrDefault(boardId, 0.0);
//        double timeScore = scoreByType.getOrDefault("time", Map.of()).getOrDefault(boardId, 0.0);
//
//        // 가중치 적용하여 최종 점수 계산
//        double finalScore = participationScore * weights.getParticipation()
//                + zzimScore * weights.getZzim()
//                + friendScore * weights.getFriend()
//                + sportTypeScore * weights.getType()
//                + timeScore * weights.getTime();
//
//        log.debug("Board {} 점수 상세 - participation: {}, zzim: {}, friend: {}, sportType: {}, time: {}, 최종: {}",
//                boardId, participationScore, zzimScore, friendScore, sportTypeScore, timeScore, finalScore);
//
//        return RecommendationDto.builder()
//                .board(BoardResponseDto.builder()
//                        .id(board.getId())
//                        .title(board.getTitle())
//                        .content(board.getContent())
//                        .maxParticipants(board.getMaxParticipants())
//                        .currentParticipants(board.getCurrentParticipants())
//                        .writer(UserSimpleDto.builder()
//                                .id(board.getWriter().getId()) // board의 작성자 정보 사용
//                                .nickname(board.getWriter().getName())
//                                .build())
//                        .build())
//                .finalScore(finalScore)
//                .participationScore(participationScore)
//                .zzimScore(zzimScore)
//                .friendScore(friendScore)
//                .typeScore(sportTypeScore) // sportType 점수로 통일
//                .timeScore(timeScore)
//                .build();
//    }
//}
