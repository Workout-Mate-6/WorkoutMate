package com.example.workoutmate.domain.chatting.entity;

import com.example.workoutmate.domain.chatting.dto.ChatroomCreateResponseDto;

public class ChattingMapper {

    // Dto ->

    // Entity -> Dto (ChatroomCreateResponseDto)
    public static ChatroomCreateResponseDto toCreateDto(Chatroom chatroom) {
        return ChatroomCreateResponseDto.builder()
                .id(chatroom.getId())
                .senderId(chatroom.getSenderId())
                .receiverId(chatroom.getReceiverId())
                .createdAt(chatroom.getCreatedAt())
                .build();
    }
}
