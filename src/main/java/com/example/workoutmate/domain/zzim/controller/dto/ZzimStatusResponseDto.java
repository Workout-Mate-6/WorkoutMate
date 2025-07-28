package com.example.workoutmate.domain.zzim.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ZzimStatusResponseDto {

    private final Long boardId;
    private final Long userId;
    private final boolean zzimmed;  // 찜 했는지 여부

    @Builder
    public ZzimStatusResponseDto(Long boardId, Long userId, boolean zzimmed) {
        this.boardId = boardId;
        this.userId = userId;
        this.zzimmed = zzimmed;
    }
}
