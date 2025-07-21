package com.example.workoutmate.domain.board.entity;


public enum SportType {
    FOOTBALL("축구"),
    BASKETBALL("농구"),
    BASEBALL("야구"),
    RUNNING("러닝"),
    TENNIS("테니스"),
    VOLLEYBALL("배구");
    // 스포츠가 더 생기면 추가 예정...

    private final String korean;

    SportType(String korean) {
        this.korean = korean;
    }

    public static SportType fromKorean(String korean) {
        for (SportType type : SportType.values()) {
            if (type.korean.equals(korean)) {
                return type;
            }
        }
        // 추후 globalException으로 수정
        throw new IllegalArgumentException("지원하지 않는 운동 종목입니다: " + korean);
    }
}
