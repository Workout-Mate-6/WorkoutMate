package com.example.workoutmate.domain.board.controller.dto;

import com.example.workoutmate.domain.board.entity.SportType;
import lombok.Getter;

@Getter
public class SearchCategoryBoardRequestDto {
    private SportType sportType;
}
