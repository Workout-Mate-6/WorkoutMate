package com.example.workoutmate.domain.chatting.entity;

import com.example.workoutmate.domain.chatting.dto.ChatRoomCreateResponseDto;

public class ChattingMapper {

    // Dto ->

    // Entity -> Dto (ChatroomCreateResponseDto)
    public static ChatRoomCreateResponseDto toCreateDto(ChatRoom chatroom) {
        return ChatRoomCreateResponseDto.builder()
                .id(chatroom.getId())
                .senderId(chatroom.getSenderId())
                .receiverId(chatroom.getReceiverId())
                .createdAt(chatroom.getCreatedAt())
                .build();
    }
}
