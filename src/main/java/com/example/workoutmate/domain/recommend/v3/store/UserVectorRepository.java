package com.example.workoutmate.domain.recommend.v3.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface UserVectorRepository extends JpaRepository<UserVectorEntity, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        INSERT INTO user_vector (user_id, vec, updated_at)
        VALUES (:userId, :vec, NOW(6)) AS new
        ON DUPLICATE KEY UPDATE
          vec = new.vec,
          updated_at = new.updated_at
        """, nativeQuery = true)
    void upsert(@Param("userId") Long userId,
                @Param("vec") byte[] vec);
}