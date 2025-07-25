package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.entity.ChatroomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatroomMemberRepository extends JpaRepository<ChatroomMember, Long> {
}
