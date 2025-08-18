package com.example.workoutmate.domain.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyApplicationResponseDto {
    private Long participationId;
    private Long boardId;
    private String boardTitle;
    private Long writerId;
    private String writerName;
    private String state;
    private LocalDateTime requestedAt;
}
