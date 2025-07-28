package com.example.workoutmate.domain.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ParticipationByBoardResponseDto {

    private final Long boardId;
    private final String title;
    private final List<ParticipationResponseDto> participations;
}
