package com.example.workoutmate.domain.participation.enums;


import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;

public enum ParticipationState {
    NONE("대기"), // 댓글 작성되면 자동으로 반응테이블에 삽입됨
    REQUESTED("신청"), // 요청 보내면 반응테이블에 삽입
    ACCEPTED("수락"), // 게시글 작성자가 선택하면 반응 테이블에 삽입
    REJECTED("거절"), // 게시글 작성자가 선택하면 반응 테이블에 삽입
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
