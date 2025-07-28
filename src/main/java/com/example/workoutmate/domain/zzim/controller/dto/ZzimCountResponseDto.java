package com.example.workoutmate.domain.zzim.controller.dto;

import lombok.Getter;

@Getter
public class ZzimCountResponseDto {

    private final Long boardId;
    private Long zzimCount;

    public ZzimCountResponseDto(Long boardId, Long zzimCount) {
        this.boardId = boardId;
        this.zzimCount = zzimCount;
    }
}
