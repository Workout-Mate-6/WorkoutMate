package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.dto.ChatroomResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatroomMemberRepositoryCustom {
    List<ChatroomResponseDto> findMyChatrooms(Long userId, LocalDateTime cursor, int size);
}
