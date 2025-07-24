package com.example.workoutmate.domain.participation.enums;


public enum ParticipationState {
    NONE("대기"),
    REQUESTED("신청"),
    PARTICIPATION("참여"),
    ACCEPTED("수락"),
    REJECTED("거절"),
    DECLINED("불참")
    ;

    private final String label;

    ParticipationState(String label) {
        this.label = label;
    }
}
