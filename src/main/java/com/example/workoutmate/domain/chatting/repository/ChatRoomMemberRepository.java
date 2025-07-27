package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long>, ChatRoomMemberRepositoryCustom {

    Optional<ChatRoomMember> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
    int countByChatRoomIdAndIsJoinedTrue(Long chatRoomId);
}
