package com.example.workoutmate.domain.participation.validation;

import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import org.springframework.stereotype.Component;

@Component
public class ParticipationValidator {

    // 같은 요청 두번 방지
    public void validateAlreadyHandled(ParticipationState current, ParticipationState requested) {
        if (current == requested) {
            throw new CustomException(CustomErrorCode.ALREADY_STATE);
        }
    }

    // 잘못된 상태 변경 방지
    public void validateParticipationTransition(
            ParticipationState currentState, ParticipationState requestedState
    ) {

        switch (currentState) {
            case ACCEPTED: // state가 이미 수락인 상태에서 거절, 신청
                if (
                        // 수락 했다가 거절은 해도 되지 않을까라는 생각...
                        requestedState == ParticipationState.REJECTED ||
                        requestedState == ParticipationState.REQUESTED
                ) {
                    throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION);
                }
                break;
            case REJECTED:
                if (
                        // 여기서도 거절 했다가 수락은...
                        requestedState == ParticipationState.ACCEPTED ||
                        requestedState == ParticipationState.DECLINED
                ) {
                    throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION);
                }
                break;

            case DECLINED:
                if (
                        requestedState == ParticipationState.ACCEPTED ||
                        requestedState == ParticipationState.REJECTED
                ) {
                    throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION);
                }
                break;
            default:
                break;
        }
    }
}
