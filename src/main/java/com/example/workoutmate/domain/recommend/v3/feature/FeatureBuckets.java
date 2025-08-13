package com.example.workoutmate.domain.recommend.v3.feature;

import java.time.LocalDateTime;

/**
 * 타입 / 시간 버킷 유틸
 */
public final class FeatureBuckets {
    private FeatureBuckets() {}
    public static String type(String sportType) { return "type:" + sportType; }
    public static String timeBucket(LocalDateTime t) {
        if (t == null) return "time:unknown";
        int h = t.getHour();
        String b = (h < 6) ? "dawn" : (h < 12) ? "morning" : (h < 18) ? "afternoon" : "evening";
        return "time:" + b;
    }
    public static String dow(LocalDateTime t) {
        if (t == null) return "dow:unknown";
        return "dow:" + t.getDayOfWeek().getValue(); // 1~7
    }

    public static String sizeBucket(Number maxParticipants) {
        int m = (maxParticipants == null) ? 0 : Math.max(0, maxParticipants.intValue());
        String b = (m <= 4) ? "s" : (m <= 10) ? "m" : "l";
        return "size:" + b;
    }
}
