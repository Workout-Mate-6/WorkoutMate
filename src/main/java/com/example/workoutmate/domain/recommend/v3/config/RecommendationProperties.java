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
        private int dim = 64; // 벡터 차원 수
        private double decayPerDay = 0.95; // 하루 경과 시 가중치 감소율
        private double userGlobalMix = 0.1; // 전역 평균 벡터를 혼합하는 비율
    }


    /**
     * 친구(Friend) 설정
     * - 친구 기반 추천 시, 친구 활동 보정치나 슬롯 주입 규칙 등을 관리.
     */
    @Getter
    @Setter
    public static class Friend {
        private int exploreDays = 14; // 최근 N일간 친구 활동 반영
        private int presenceCap = 3; // 친구 등장 보정 적용 최대 횟수
        private double presenceBonusPerFriend = 3.3; // percent // 친구 1명당 점수 보정치(퍼센트)
        private double presenceBonusCap = 10.0;      // percent // 친구 보정치 최대값(퍼센트)
        private List<Integer> injectSlots = List.of(2, 6); // 친구 게시글을 주입할 추천 결과 위치(인덱스)
        private double injectMinSim = 0.30;  // 친구 추천 주입 최소 유사도 기준
    }


    /**
     * 시간(Time) 설정
     * - 게시글 시작 시간과 현재 시간의 차이를 기반으로 가중치를 부여하는 설정.
     */
    @Getter
    @Setter
    public static class Time {
        private boolean rerankEnabled = true; // 시간 기반 재정렬 활성화 여부
        private double maxMultiplier = 1.15; // 최대 가중치 배율
        private double centerHours = 24; // 가중치 중심 시간(예: 이벤트 시작 전후 24시간)
        private double slope = 6; // 시간 가중치 기울기
    }


    /**
     * 추천 규칙(Rules) 설정
     * - 정원 초과, 정원 임박 등의 상태를 필터링하거나 페널티를 부여하는 규칙.
     */
    @Getter
    @Setter
    public static class Rules {
        private boolean excludeFull = true; // 정원 꽉 찬 게시글 제외 여부
        private double nearFullThreshold = 0.9; // 정원 임박 판정 기준 (90% 이상)
        private double nearFullPenaltyPercent = 2.0; // 정원 임박 시 점수 감소 비율
    }


    /**
     * 다양성(Diversify) 설정
     * - 동일 카테고리(종목) 반복 추천 방지와 최소 종목 다양성 확보.
     */
    @Getter
    @Setter
    public static class Diversify {
        private int perTypeMax = 4; // 같은 종목 최대 연속 추천 개수
        private int minDistinctTypes = 2; // 최소 포함 종목 수
    }
}
