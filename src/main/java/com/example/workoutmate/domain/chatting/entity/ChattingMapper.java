package com.example.workoutmate.domain.chatting.entity;

import com.example.workoutmate.domain.chatting.dto.ChatMessageResponseDto;
import com.example.workoutmate.domain.chatting.dto.ChatRoomCreateResponseDto;

public class ChattingMapper {

    // Entity -> Dto (ChatroomCreateResponseDto)
    public static ChatRoomCreateResponseDto toCreateDto(ChatRoom chatroom) {
        return ChatRoomCreateResponseDto.builder()
                .id(chatroom.getId())
                .senderId(chatroom.getSenderId())
                .receiverId(chatroom.getReceiverId())
                .createdAt(chatroom.getCreatedAt())
                .build();
    }

    // Entity -> Dto (ChatMessageResponseDto)
    public static ChatMessageResponseDto toMessageDto(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .id(chatMessage.getId())
                .chatRoomId(chatMessage.getChatRoomId())
                .senderId(chatMessage.getSender().getId())
                .senderName(chatMessage.getSender().getName())
                .message(chatMessage.getMessage())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}
