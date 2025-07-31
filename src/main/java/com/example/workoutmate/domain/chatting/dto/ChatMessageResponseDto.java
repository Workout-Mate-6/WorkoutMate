package com.example.workoutmate.domain.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {

    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String message;
    private LocalDateTime createdAt;
}
