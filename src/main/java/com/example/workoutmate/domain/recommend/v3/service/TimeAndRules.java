package com.example.workoutmate.domain.recommend.v3.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.recommend.v3.config.RecommendationProperties;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class TimeAndRules {
    public static double urgencyMultiplier(Board b, RecommendationProperties p) {
        if (!p.getTime().isRerankEnabled()) return 1.0;
        if (b.getStartTime() == null) return 1.0;
        double center = p.getTime().getCenterHours();
        double slope = p.getTime().getSlope();
        double max = p.getTime().getMaxMultiplier();
        double dtHours = Duration.between(LocalDateTime.now(), b.getStartTime()).toHours();
        double u = 1.0 / (1.0 + Math.exp((dtHours - center) / slope));
        return Math.min(max, u);
    }

    public static boolean excludeByFull(Board b, RecommendationProperties p) {
        if (!p.getRules().isExcludeFull()) return false;
        Long max = b.getMaxParticipants();
        Long cur = b.getCurrentParticipants();
        if (max == null || max <= 0 || cur == null) return false;
        return cur >= max;
    }

    public static double nearFullPenaltyPercent(Board b, RecommendationProperties p) {
        Long max = b.getMaxParticipants();
        Long cur = b.getCurrentParticipants();
        if (max == null || max <= 0 || cur == null) return 0.0;
        double occ = Math.min(1.0, cur / (double) max);
        if (occ >= p.getRules().getNearFullThreshold()) return p.getRules().getNearFullPenaltyPercent();
        return 0.0;
    }

    public static List<Board> diversifyQuota(List<Board> sorted, int perTypeMax, int minDistinctTypes, int limit) {
        Map<String, Integer> cnt = new HashMap<>();
        LinkedHashSet<Long> used = new LinkedHashSet<>();
        List<Board> out = new ArrayList<>();

        // 1) 최소 서로 다른 타입 수 보장: 처음 보는 타입을 우선 채운다
        for (Board b : sorted) {
            if (out.size() >= Math.max(0, minDistinctTypes)) break;
            String type = String.valueOf(b.getSportType());
            if (!cnt.containsKey(type)) {
                if (used.add(b.getId())) {
                    out.add(b);
                    cnt.put(type, 1);
                }
            }
        }

        // 2) 나머지 채우기: 타입별 perTypeMax 제한 지키면서 상위에서 순차 채움
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
