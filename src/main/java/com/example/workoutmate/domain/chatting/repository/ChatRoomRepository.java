package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.entity.ChatRoom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ChatRoom c " +
            "WHERE c.user1Id = :user1 AND c.user2Id = :user2 " +
            "AND c.isDeleted = false")
    Optional<ChatRoom> findByUsersAndNotDeletedWithLock(@Param("user1") Long user1,
                                                         @Param("user2") Long user2);

    Optional<ChatRoom> findByIdAndIsDeletedFalse(Long chatRoomId);
}
