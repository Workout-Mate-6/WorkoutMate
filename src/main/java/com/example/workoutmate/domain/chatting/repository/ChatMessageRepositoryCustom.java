package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepositoryCustom {
    List<ChatMessage> findChatRoomMessages(Long chatRoomId, Long cursor, int size);
}
