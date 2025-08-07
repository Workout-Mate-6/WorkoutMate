package com.example.workoutmate.domain.board.dto;

import com.example.workoutmate.domain.board.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PopularBoardDto {
    private Long id;
    private Long writerId;
    private String title;
    private String content;
    private String sportType;
    private Long maxParticipants;
    private Long currentParticipants;
    private Integer viewCount;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
