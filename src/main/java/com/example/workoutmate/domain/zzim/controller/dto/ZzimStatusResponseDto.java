package com.example.workoutmate.domain.zzim.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ZzimStatusResponseDto {

    private final Long boardId;
    private final Long userId;
    private final boolean zzimmed; // 찜 했는지 여부
    private final Long zzimId;

    @Builder
    public ZzimStatusResponseDto(Long boardId, Long userId, boolean zzimmed, Long zzimId) {
        this.boardId = boardId;
        this.userId = userId;
        this.zzimmed = zzimmed;
        this.zzimId = zzimId;
    }
}
