package com.example.workoutmate.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BoardSportTypeResponseDto {

    private List<String> sportTypes;
}
