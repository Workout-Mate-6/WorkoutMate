package com.example.workoutmate.domain.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class CombinedParticipationViewResponse {
    private Page<ParticipationByBoardResponseDto> writerSide;   // 내가 쓴 글에 온 요청들
    private Page<MyApplicationResponseDto> applicantSide;       // 내가 신청한 요청들
}
