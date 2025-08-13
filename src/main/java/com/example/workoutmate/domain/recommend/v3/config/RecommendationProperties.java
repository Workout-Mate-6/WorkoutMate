package com.example.workoutmate.domain.recommend.v3.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@ConfigurationProperties(prefix = "recommendation")
@Getter
@Setter
public class RecommendationProperties {
    private Vector vector = new Vector();
    private Friend friend = new Friend();
    private Time time = new Time();
    private Rules rules = new Rules();
    private Diversify diversify = new Diversify();

    @Getter
    @Setter
    public static class Vector {
        private int dim = 32;
        private double decayPerDay = 0.95;
        private double userGlobalMix = 0.1;
    }

    @Getter
    @Setter
    public static class Friend {
        private int exploreDays = 14;
        private int presenceCap = 3;
        private double presenceBonusPerFriend = 3.3; // percent
        private double presenceBonusCap = 10.0;      // percent
        private List<Integer> injectSlots = List.of(2, 6);
        private double injectMinSim = 0.30;
    }

    @Getter
    @Setter
    public static class Time {
        private boolean rerankEnabled = true;
        private double maxMultiplier = 1.15;
        private double centerHours = 24;
        private double slope = 6;
    }

    @Getter
    @Setter
    public static class Rules {
        private boolean excludeFull = true;
        private double nearFullThreshold = 0.9;
        private double nearFullPenaltyPercent = 2.0;
    }

    @Getter
    @Setter
    public static class Diversify {
        private int perTypeMax = 4;
        private int minDistinctTypes = 2;
    }
}
