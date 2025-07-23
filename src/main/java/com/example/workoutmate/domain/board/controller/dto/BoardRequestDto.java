package com.example.workoutmate.domain.board.controller.dto;

import com.example.workoutmate.domain.board.entity.SportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BoardRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
    @NotNull(message = "운동 종목은 필수입니다.")
    private SportType sportType;

    public BoardRequestDto(String title, String content, SportType sportType) {
        this.title = title;
        this.content = content;
        this.sportType = sportType;
    }
}
