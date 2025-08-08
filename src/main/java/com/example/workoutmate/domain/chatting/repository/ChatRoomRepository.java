package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c FROM ChatRoom c " +
            "WHERE ((c.senderId = :user1 AND c.receiverId = :user2) " +
            "   OR (c.senderId = :user2 AND c.receiverId = :user1)) " +
            "AND c.isDeleted = false")
    Optional<ChatRoom> findByUsersAndNotDeleted(@Param("user1") Long user1, @Param("user2") Long user2);

    Optional<ChatRoom> findByIdAndIsDeletedFalse(Long chatRoomId);
}
