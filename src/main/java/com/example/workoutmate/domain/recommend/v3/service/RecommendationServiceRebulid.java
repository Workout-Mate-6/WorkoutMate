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
    private record Scored(Board b, double basePercent) {}

    private final RecommendationProperties props;   // 네 설정 클래스 이름
    private final BoardService boardService;
    private final UserVectorService userVectorService;
    private final BoardVectorService boardVectorService;
    private final FriendSignalsService friendSignalsService;

    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendations(Long userId, int limit) {
        // 후보 수집 (+ FULL 제외)
        int maxCandidate = 1000;
        var pageable = PageRequest.of(0, maxCandidate);
        List<Board> candidates = boardService.findRecommendationCandidates(userId, pageable);
        if (candidates.isEmpty()) return List.of();

        candidates = candidates.stream()
                .filter(b -> !TimeAndRules.excludeByFull(b, props))
                .toList();
        if (candidates.isEmpty()) return List.of();

        // 유저/보드 벡터, 친구 신호
        float[] U = userVectorService.getOrBuild(userId);
        Map<Long, Integer> friendCounts = friendSignalsService.friendCounts(userId, candidates);
        Set<String> friendExploreTypes = friendSignalsService.friendExploreTypes(userId);

        // 각 보드 매칭 % 계산(코사인 + 임박 곱 + 친구 가산 + near-full 패널티)
        List<Scored> scored = new ArrayList<>(candidates.size());
        Map<Long, float[]> vecByBoard = boardVectorService.getOrEncodeBulk(candidates);
        for (Board b : candidates) {
            float[] I = vecByBoard.get(b.getId());
            double sim = Math.max(0, VectorUtils.dot(U, I)); // 0~1
            double percent = Math.round(sim * 100);          // 0~100

            // 임박 보정(곱)
            percent *= TimeAndRules.urgencyMultiplier(b, props);

            // 친구 가산(%)
            int fc = friendCounts.getOrDefault(b.getId(), 0);
            percent += Math.min(
                    props.getFriend().getPresenceBonusCap(),
                    fc * props.getFriend().getPresenceBonusPerFriend()
            );

            // 만석 근접 패널티(%)
            percent -= TimeAndRules.nearFullPenaltyPercent(b, props);

            percent = Math.max(0, Math.min(100, percent));
            scored.add(new Scored(b, percent));
        }

        // 정렬 → 다양화 → 친구 탐색 슬롯 주입
        scored.sort(Comparator.comparingDouble(Scored::basePercent).reversed());

        List<Board> diversified = TimeAndRules.diversifyQuota(
                scored.stream().map(Scored::b).toList(),
                props.getDiversify().getPerTypeMax(),
                props.getDiversify().getMinDistinctTypes(),
                limit
        );

        List<Board> injected = injectFriendExplore(diversified, scored, friendExploreTypes, limit);

        // DTO( boardId / matchPercent / reasons )로 매핑
        Map<Long, Double> percentById = scored.stream()
                .collect(Collectors.toMap(s -> s.b().getId(), Scored::basePercent));

        List<RecommendationDto> out = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, injected.size()); i++) {
            Board b = injected.get(i);
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
                    .board(BoardResponseDto.from(b))   // ← 중첩으로 세팅
                    .build());
        }
        return out;
    }

    /** 친구 탐색 타입 주입 (record Scored 사용) */
    private List<Board> injectFriendExplore(
            List<Board> base,
            List<Scored> scored,
            Set<String> exploreTypes,
            int limit
    ) {
        if (exploreTypes.isEmpty()) return base.stream().limit(limit).toList();

        List<Integer> slots = props.getFriend().getInjectSlots();
        List<Board> pool  = new ArrayList<>(base);

        List<Board> candidates = scored.stream()
                .filter(s -> exploreTypes.contains(String.valueOf(s.b().getSportType())))
                .filter(s -> s.basePercent() >= props.getFriend().getInjectMinSim() * 100.0)
                .sorted(Comparator.comparingDouble(Scored::basePercent).reversed())
                .map(Scored::b)
                .toList();

        int ci = 0;
        for (Integer slot : slots) {
            if (ci >= candidates.size()) break;
            if (slot < 0 || slot >= Math.min(limit, pool.size())) continue;

            Board cand = candidates.get(ci++);
            boolean exists = pool.stream().anyMatch(b -> b.getId().equals(cand.getId()));
            if (!exists) {
                if (slot < pool.size()) pool.add(slot, cand); else pool.add(cand);
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
}

