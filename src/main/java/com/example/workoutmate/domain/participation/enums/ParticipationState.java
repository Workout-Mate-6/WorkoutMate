package com.example.workoutmate.domain.participation.enums;


import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;

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

    public String getLabel() {
        return label;
    }

    public static ParticipationState of(String value) {
        for (ParticipationState state : values()) {
            if (state.name().equalsIgnoreCase(value)) {
                return state;
            }
        }

        for (ParticipationState state : values()) {
            if (state.getLabel().equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new CustomException(CustomErrorCode.PARTICIPATION_NOT_FOUND);
    }
}
