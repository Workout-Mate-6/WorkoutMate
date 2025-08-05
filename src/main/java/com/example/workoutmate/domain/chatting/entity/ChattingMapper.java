package com.example.workoutmate.domain.chatting.entity;

import com.example.workoutmate.domain.chatting.dto.ChatDto;
import com.example.workoutmate.domain.chatting.dto.ChatMessageResponseDto;
import com.example.workoutmate.domain.chatting.dto.ChatRoomCreateResponseDto;
import com.example.workoutmate.domain.user.entity.User;

import java.time.LocalDateTime;

public class ChattingMapper {

    // Dto -> Entity (ChatMessage)
    public static ChatMessage toChatMessage(ChatDto chatDto, User user) {
        return ChatMessage.builder()
                .chatRoomId(chatDto.getChatRoomId())
                .sender(user)
                .message(chatDto.getMessage())
                .createdAt(LocalDateTime.now())
                .type(chatDto.getType())
                .build();
    }

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

    // Entity -> Dto (ChatDto)
    public static ChatDto toChatDto(ChatMessage chatMessage) {
        return ChatDto.builder()
                .chatRoomId(chatMessage.getChatRoomId())
                .senderId(chatMessage.getSender().getId())
                .senderName(chatMessage.getSender().getName())
                .message(chatMessage.getMessage())
                .createdAt(chatMessage.getCreatedAt().toString())
                .type(chatMessage.getType())
                .build();
    }
}
