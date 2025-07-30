package com.example.workoutmate.domain.comment.dto;

import com.example.workoutmate.domain.participation.enums.ParticipationState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentWithParticipationStatusResponseDto {

    private Long commentId;
    private Long userId;
    private String nickname;
    private String content;
    private ParticipationState state;
}
