package com.example.workoutmate.domain.participation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipationRequestDto {

    @NotNull
    private String state;

    @Override
    public String toString() {
        return "요청을 " + state + " 하였습니다.";
    }
}
