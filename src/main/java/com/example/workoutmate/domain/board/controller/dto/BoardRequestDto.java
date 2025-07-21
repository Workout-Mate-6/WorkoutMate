package com.example.workoutmate.domain.board.controller.dto;

import com.example.workoutmate.domain.board.entity.SportType;
import lombok.Getter;

@Getter
public class BoardRequestDto {

    private String title;
    private String content;
    private SportType sportType;

    public BoardRequestDto(String title, String content, SportType sportType) {
        this.title = title;
        this.content = content;
        this.sportType = sportType;
    }
}
