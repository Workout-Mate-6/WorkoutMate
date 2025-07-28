package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.dto.ChatRoomResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRoomMemberRepositoryCustom {
    List<ChatRoomResponseDto> findMyChatRooms(Long userId, LocalDateTime cursor, int size);
}
