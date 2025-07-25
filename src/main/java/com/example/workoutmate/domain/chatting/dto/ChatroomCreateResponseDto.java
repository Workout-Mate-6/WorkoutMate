package com.example.workoutmate.domain.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChatroomCreateResponseDto {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime createdAt;
}
