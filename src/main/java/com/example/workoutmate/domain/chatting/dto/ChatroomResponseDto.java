package com.example.workoutmate.domain.chatting.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatroomResponseDto {

    private Long id;
    private String opponentName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    @QueryProjection
    public ChatroomResponseDto(Long id, String opponentName, String lastMessage, LocalDateTime lastMessageTime) {
        this.id = id;
        this.opponentName = opponentName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }
}
