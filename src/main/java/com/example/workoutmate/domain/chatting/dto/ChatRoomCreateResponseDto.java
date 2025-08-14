package com.example.workoutmate.domain.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomCreateResponseDto {

    private Long id;
    private Long user1d;
    private Long user2Id;
    private LocalDateTime createdAt;
}
