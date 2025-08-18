package com.example.workoutmate.domain.recommend.v3.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.recommend.v3.config.RecommendationProperties;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class TimeAndRules {

    /**
     * 시간 임박 가중치 계산
     * - 추천 점수를 재랭킹할 때, 시작 시간이 가까운 게시글에 가중치를 더 줌
     * - 로지스틱 함수(시그모이드) 형태로 시간 차이에 따라 가중치가 변함
     * - 요약 : 시간 임박도에 따라 점수 보장
     */
    public static double urgencyMultiplier(Board b, RecommendationProperties p) {
        if (!p.getTime().isRerankEnabled()) return 1.0; // 시간 재 랭킹 기능 | 비활성시 1.0(변화 없음)
        if (b.getStartTime() == null) return 1.0; // 시작시간이 없으면 변화 없음

        double center = p.getTime().getCenterHours(); // 시그모이드 중아점
        double slope = p.getTime().getSlope(); // 곡선 기울기
        double max = p.getTime().getMaxMultiplier(); // 가중치 최대값

        // 현재 시각과 시작 시간의 차이를 '시간' 단위로 계산
        double dtHours = Duration.between(LocalDateTime.now(), b.getStartTime()).toHours();

        // 시그모이드 함수로 임박 가중치 계산
        double u = 1.0 / (1.0 + Math.exp((dtHours - center) / slope));
        return Math.min(max, u);
    }


    /**
     * 정원(최대 인원)에 도달했을 경우 제외할지 여부 판단
     * - 요약 : 꽉 찬 모집글 필터링
     */
    public static boolean excludeByFull(Board b, RecommendationProperties p) {
        if (!p.getRules().isExcludeFull()) return false;
        Long max = b.getMaxParticipants();
        Long cur = b.getCurrentParticipants();
        if (max == null || max <= 0 || cur == null) return false;
        return cur >= max;
    }

    /**
     * 거의 정원에 찼을 때 추천 점수에서 깎을 페널티 비율(%) 계산
     * - 요약 : 거의 꽉 찬 글에는 점수 감점
     */
    public static double nearFullPenaltyPercent(Board b, RecommendationProperties p) {
        Long max = b.getMaxParticipants();
        Long cur = b.getCurrentParticipants();

        if (max == null || max <= 0 || cur == null) return 0.0;

        // 점유율 계산 (1.0 이상이면 1.0으로 고정)
        double occ = Math.min(1.0, cur / (double) max);
        if (occ >= p.getRules().getNearFullThreshold()) return p.getRules().getNearFullPenaltyPercent();
        return 0.0;
    }

    /**
     * 추천 결과 다양성 확보를 위한 타입별 제한 적용
     * - 입력: 이미 점수순으로 정렬된 게시글 목록(sorted)
     * - 1단계: 최소 서로 다른 타입(minDistinctTypes) 수 보장
     * - 2단계: 타입별 최대 개수(perTypeMax) 제한을 지키며 limit 개수까지 채움
     * - 요약 : 추천 결과에서 특정 타입 쏠림 방지
     */
    public static List<Board> diversifyQuota(List<Board> sorted, int perTypeMax, int minDistinctTypes, int limit) {
        Map<String, Integer> cnt = new HashMap<>(); // 타입별 개수 카운트
        LinkedHashSet<Long> used = new LinkedHashSet<>();
        List<Board> out = new ArrayList<>();

        // 최소 서로 다른 타입 수 보장: 처음 보는 타입을 우선 채운다
        for (Board b : sorted) {
            if (out.size() >= Math.max(0, minDistinctTypes)) break;
            String type = String.valueOf(b.getSportType());
            if (!cnt.containsKey(type)) { // 처음 등장한 타입이면
                if (used.add(b.getId())) { // 아직 선택 안한 게시글이면
                    out.add(b);
                    cnt.put(type, 1);
                }
            }
        }

        // 나머지 채우기: 타입별 perTypeMax 제한 지키면서 상위에서 순차 채움
        for (Board b : sorted) {
            if (out.size() >= limit) break;
            if (used.contains(b.getId())) continue;
            String type = String.valueOf(b.getSportType());
            if (cnt.getOrDefault(type, 0) >= perTypeMax) continue;
            out.add(b);
            used.add(b.getId());
            cnt.put(type, cnt.getOrDefault(type, 0) + 1);
        }
        return out;
    }
}
