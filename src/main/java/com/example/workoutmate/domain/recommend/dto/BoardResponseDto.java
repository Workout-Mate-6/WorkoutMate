package com.example.workoutmate.domain.recommend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardResponseDto {

    private Long id;
    private String title;
    private String content;
    private Long maxParticipants;
    private Long currentParticipants;
    private UserSimpleDto writer;
}

