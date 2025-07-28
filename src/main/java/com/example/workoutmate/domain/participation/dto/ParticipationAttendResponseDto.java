package com.example.workoutmate.domain.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipationAttendResponseDto {

    private final String username;
    private final String state;
}
