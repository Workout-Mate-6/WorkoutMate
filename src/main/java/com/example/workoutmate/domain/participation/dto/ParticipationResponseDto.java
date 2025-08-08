package com.example.workoutmate.domain.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ParticipationResponseDto { // 참여 요청 DTO

    private Long participationId;
    private String username;
    private final String state;
    private final LocalDateTime requestAt;
}
