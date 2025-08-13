package com.example.workoutmate.domain.chatting.dto;

import com.example.workoutmate.domain.chatting.enums.MessageType;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDto {

    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String message;
    private String createdAt;
    private MessageType type;

    public void setTypeTalk() {
        this.type = MessageType.TALK;
    }

    public void setTypeEnter() {
        this.type = MessageType.ENTER;
    }

    public void setTypeLeave() {
        this.type = MessageType.LEAVE;
    }
}
