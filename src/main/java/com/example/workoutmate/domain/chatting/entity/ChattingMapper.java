package com.example.workoutmate.domain.chatting.entity;

import com.example.workoutmate.domain.chatting.dto.ChatDto;
import com.example.workoutmate.domain.chatting.dto.ChatMessageResponseDto;
import com.example.workoutmate.domain.chatting.dto.ChatRoomCreateResponseDto;
import com.example.workoutmate.domain.chatting.enums.MessageType;
import com.example.workoutmate.domain.user.entity.User;

import java.time.LocalDateTime;

public class ChattingMapper {

    // Entity -> Entity
    public static ChatMessage memberToStartMessage(ChatRoomMember chatRoomMember, User sender) {
        return ChatMessage.builder()
                .chatRoomId(chatRoomMember.getChatRoomId())
                .sender(sender)
                .message(sender.getName() + "님이 대화를 시작하였습니다.")
                .createdAt(chatRoomMember.getJoinedAt())
                .type(MessageType.ENTER)
                .build();
    }

    public static ChatMessage memberToJoinMessage(ChatRoomMember chatRoomMember, User sender) {
        return ChatMessage.builder()
                .chatRoomId(chatRoomMember.getChatRoomId())
                .sender(sender)
                .message(sender.getName() + "님이 입장했습니다.")
                .createdAt(chatRoomMember.getJoinedAt())
                .type(MessageType.ENTER)
                .build();
    }

    public static ChatMessage memberToLeaveMessage(ChatRoomMember chatRoomMember, User sender) {
        return ChatMessage.builder()
                .chatRoomId(chatRoomMember.getChatRoomId())
                .sender(sender)
                .message(sender.getName() + "님이 퇴장했습니다.")
                .createdAt(chatRoomMember.getLeftAt())
                .type(MessageType.LEAVE)
                .build();
    }

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
                .user1d(chatroom.getUser1Id())
                .user2Id(chatroom.getUser2Id())
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
                .type(chatMessage.getType())
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
