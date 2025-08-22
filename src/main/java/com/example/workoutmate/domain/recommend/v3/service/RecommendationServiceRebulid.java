package com.example.workoutmate.domain.recommend.v3.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.service.BoardService;
import com.example.workoutmate.domain.recommend.v3.config.RecommendationProperties;
import com.example.workoutmate.domain.recommend.v3.dto.BoardResponseDto;
import com.example.workoutmate.domain.recommend.v3.dto.RecommendationDto;
import com.example.workoutmate.domain.recommend.v3.vector.VectorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 추천 시나리오의 오케스트레이션.
 * 1) 후보 게시글 페이징 조회(작성자 즉시 로딩으로 N+1 방지)
 * 2) 게시글 벡터 벌크 로딩(캐시-우선, 미스만 인코딩)
 * 3) 사용자 벡터/친구 정보/참여 이력 등 신호를 사용해 스코어 산출
 * 4) 사유(reasons)와 함께 RecommendationDto로 매핑
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceRebulid {

    // 클래스 레벨 record (다른 메서드에서도 동일 타입)
    private record Scored(Board b, double basePercent) {
    }

    private final RecommendationProperties props;   // 네 설정 클래스 이름
    private final BoardService boardService;
    private final UserVectorService userVectorService;
    private final BoardVectorService boardVectorService;
    private final FriendSignalsService friendSignalsService;

    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendations(Long userId, int limit) {
        int maxCandidate = 1000;
        var pageable = PageRequest.of(0, maxCandidate);
        List<Board> candidates = boardService.findRecommendationCandidates(userId, pageable);
        if (candidates.isEmpty()) return List.of();

        candidates = candidates.stream()
                .filter(b -> !TimeAndRules.excludeByFull(b, props))
                .toList();
        if (candidates.isEmpty()) return List.of();

        float[] U = userVectorService.getOrBuild(userId);
        // 벡터 단위 보장 (L2 정규화)
        VectorUtils.l2Normalize(U);

        Map<Long, Integer> friendCounts = friendSignalsService.friendCounts(userId, candidates);
        Set<String> friendExploreTypes = friendSignalsService.friendExploreTypes(userId);

        List<Scored> scored = calculateBoardScores(candidates, U, friendCounts);

        // 동점 시 게시글 ID로 2차 정렬 (안정적 순서)
        scored.sort(Comparator.comparingDouble(Scored::basePercent).reversed()
                .thenComparing(s -> s.b().getId(), Comparator.reverseOrder()));

        List<Board> diversified = TimeAndRules.diversifyQuota(
                scored.stream().map(Scored::b).toList(),
                props.getDiversify().getPerTypeMax(),
                props.getDiversify().getMinDistinctTypes(),
                limit
        );

        List<Board> injected = injectFriendExplore(diversified, scored, friendExploreTypes, limit);

        // 주입 후 다양화 규칙 재적용
        List<Board> finalResult = TimeAndRules.diversifyQuota(
                injected,
                props.getDiversify().getPerTypeMax(),
                props.getDiversify().getMinDistinctTypes(),
                limit
        );

        return convertToRecommendationDtos(finalResult, scored, friendCounts, friendExploreTypes, limit);
    }
    /**
     * 친구 탐색 타입 주입 (record Scored 사용)
     */
    private List<Board> injectFriendExplore(
            List<Board> base,
            List<Scored> scored,
            Set<String> exploreTypes,
            int limit
    ) {
        if (exploreTypes.isEmpty()) return base.stream().limit(limit).toList();

        List<Integer> slots = props.getFriend().getInjectSlots();
        List<Board> pool = new ArrayList<>(base);

        List<Board> candidates = scored.stream()
                .filter(s -> exploreTypes.contains(String.valueOf(s.b().getSportType())))
                .filter(s -> s.basePercent() >= props.getFriend().getInjectMinSim() * 100.0)
                .sorted(Comparator.comparingDouble(Scored::basePercent).reversed())
                .map(Scored::b)
                .toList();

        Set<Long> poolIds = pool.stream().map(Board::getId).collect(Collectors.toSet());

        int ci = 0;
        for (Integer slot : slots) {
            if (ci >= candidates.size()) break;
            if (slot < 0 || slot >= Math.min(limit, pool.size())) continue;

            Board cand = candidates.get(ci++);
            if (!poolIds.contains(cand.getId())) {
                if (slot < pool.size()) {
                    pool.add(slot, cand);
                    poolIds.add(cand.getId());
                } else {
                    pool.add(cand);
                    poolIds.add(cand.getId());
                }
            }
        }

        LinkedHashSet<Long> seen = new LinkedHashSet<>();
        List<Board> out = new ArrayList<>();
        for (Board b : pool) {
            if (seen.add(b.getId())) out.add(b);
            if (out.size() >= limit) break;
        }
        return out;
    }

    private List<RecommendationDto> convertToRecommendationDtos(
            List<Board> finalBoards,
            List<Scored> scored,
            Map<Long, Integer> friendCounts,
            Set<String> friendExploreTypes,
            int limit) {

        Map<Long, Double> percentById = scored.stream()
                .collect(Collectors.toMap(s -> s.b().getId(), Scored::basePercent));

        List<RecommendationDto> out = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, finalBoards.size()); i++) {
            Board b = finalBoards.get(i);
            int mp = (int) Math.round(percentById.getOrDefault(b.getId(), 0.0));

            List<String> reasons = new ArrayList<>();
            reasons.add("너의 운동패턴과 " + mp + "% 일치");

            int fc = friendCounts.getOrDefault(b.getId(), 0);
            if (fc > 0) reasons.add("친구 " + fc + "명 참여 중");
            if (friendExploreTypes.contains(String.valueOf(b.getSportType())))
                reasons.add("친구들이 최근 이 종목을 해봤어요");

            out.add(RecommendationDto.builder()
                    .boardId(b.getId())
                    .matchPercent(mp)
                    .reasons(reasons)
                    .board(BoardResponseDto.from(b))
                    .build());
        }
        return out;
    }

    private List<Scored> calculateBoardScores(List<Board> candidates, float[] userVector, Map<Long, Integer> friendCounts) {
        List<Scored> scored = new ArrayList<>();
        Map<Long, float[]> vecByBoard = boardVectorService.getOrEncodeBulk(candidates);

        for (Board b : candidates) {
            float[] I = vecByBoard.get(b.getId());
            if (I == null) continue;

            // 보드 벡터도 단위 보장
            VectorUtils.l2Normalize(I);

            // 기본 유사도 (정밀도 유지)
            double score = Math.max(0.0, VectorUtils.dot(userVector, I));
            if (!Double.isFinite(score)) score = 0.0;

            // 시간 임박도 보정 (상한 적용)
            double urgency = TimeAndRules.urgencyMultiplier(b, props);
            urgency = Math.min(urgency, props.getTime().getMaxMultiplier()); // 상한 보장
            if (!Double.isFinite(urgency)) urgency = 1.0;
            score *= urgency;

            // 친구 참여 보너스 (덧셈, 0~1 범위로 정규화)
            int fc = friendCounts.getOrDefault(b.getId(), 0);
            double friendBonus = Math.min(
                    props.getFriend().getPresenceBonusCap(),
                    fc * props.getFriend().getPresenceBonusPerFriend()
            ) / 100.0; // 퍼센트를 0~1로 변환
            if (!Double.isFinite(friendBonus)) friendBonus = 0.0;
            score += friendBonus;

            // 정원 임박 패널티 (뺄셈, 0~1 범위로 정규화)
            double penalty = TimeAndRules.nearFullPenaltyPercent(b, props) / 100.0;
            if (!Double.isFinite(penalty)) penalty = 0.0;
            score -= penalty;

            // 최종 점수 범위 보정 및 퍼센트 변환
            score = Math.max(0.0, Math.min(1.0, score));
            double percent = Math.round(score * 100.0);

            scored.add(new Scored(b, percent));
        }

        return scored;
    }
}

