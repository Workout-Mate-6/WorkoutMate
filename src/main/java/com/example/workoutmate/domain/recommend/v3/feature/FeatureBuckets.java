package com.example.workoutmate.domain.recommend.v3.feature;

import java.time.LocalDateTime;

/**
 * 타입 / 시간 버킷 유틸
 */
public final class FeatureBuckets {
    private FeatureBuckets() {
    }


    /**
     * 스포츠 종류를 feature key로 변환
     * ex) "SOCCER" → "type:SOCCER"
     */
    public static String type(String sportType) {
        return "type:" + sportType;
    }


    /**
     * 시간대(time of day)를 bucket으로 변환
     * - 0~5시   → dawn (새벽)
     * - 6~11시  → morning (아침)
     * - 12~17시 → afternoon (오후)
     * - 18~23시 → evening (저녁)
     * null이면 "time:unknown" 반환
     */
    public static String timeBucket(LocalDateTime t) {
        if (t == null) return "time:unknown";
        int h = t.getHour();
        String b = (h < 6) ? "dawn" : (h < 12) ? "morning" : (h < 18) ? "afternoon" : "evening";
        return "time:" + b;
    }


    /**
     * 요일(Day of Week)을 feature key로 변환
     * - java.time.DayOfWeek.getValue() → 1(월) ~ 7(일)
     * ex) 월요일 → "dow:1", 금요일 → "dow:5"
     * null이면 "dow:unknown" 반환
     */
    public static String dow(LocalDateTime t) {
        if (t == null) return "dow:unknown";
        return "dow:" + t.getDayOfWeek().getValue(); // 1~7
    }


    /**
     * 최대 인원 수(maxParticipants)를 크기(bucket)으로 변환
     * - 0~4  → s (small)
     * - 5~10 → m (medium)
     * - 11+  → l (large)
     * null이면 0 처리
     */
    public static String sizeBucket(Number maxParticipants) {
        int m = (maxParticipants == null) ? 0 : Math.max(0, maxParticipants.intValue());
        String b = (m <= 4) ? "s" : (m <= 10) ? "m" : "l";
        return "size:" + b;
    }
}
