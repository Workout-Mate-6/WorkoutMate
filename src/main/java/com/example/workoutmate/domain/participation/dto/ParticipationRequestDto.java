package com.example.workoutmate.domain.participation.dto;

import com.example.workoutmate.domain.participation.enums.ParticipationState;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ParticipationRequestDto {

    @NotNull
    private ParticipationState state;
}
