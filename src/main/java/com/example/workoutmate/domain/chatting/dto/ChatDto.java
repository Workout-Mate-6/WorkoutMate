package com.example.workoutmate.domain.chatting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDto {

    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String message;
    private String createdAt;
}
