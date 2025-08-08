package com.example.workoutmate.domain.board.dto;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.board.enums.Status;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponseDto {

    private final Long id;
    private final Long writerId;
    private final String title;
    private final String content;
    private final SportType sportType;
    private final Long maxParticipants;
    private final Long currentParticipants;
    private final Status status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

}
