package com.example.workoutmate.domain.zzim.controller.dto;

import com.example.workoutmate.domain.zzim.entity.Zzim;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ZzimResponseDto {

    private final Long zzimId;
    private final Long boardId;
    private final Long userId;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public ZzimResponseDto(Zzim zzim) {
        this.zzimId = zzim.getId();
        this.boardId = zzim.getBoard().getId();
        this.userId = zzim.getUser().getId();
        this.createdAt = zzim.getCreatedAt();
        this.modifiedAt = zzim.getModifiedAt();
    }
}
