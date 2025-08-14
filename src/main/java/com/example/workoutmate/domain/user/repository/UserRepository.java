package com.example.workoutmate.domain.user.repository;

import com.example.workoutmate.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailAndIsDeletedFalse(String email);
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByIdAndIsDeletedFalseAndIsEmailVerifiedTrue(Long id);


    boolean existsByEmailAndIsDeletedFalseAndIsEmailVerifiedTrue(String email);

    Optional<User> findByEmailAndIsDeletedFalseAndIsEmailVerifiedFalse(String email);

    Optional<User> findByEmailAndIsDeletedFalseAndIsEmailVerifiedTrue(String email);

    @Query("SELECT u FROM User u WHERE u.isEmailVerified = false AND u.createdAt < :time")
    Page<User> findUnverifiedUsers(@Param("time")LocalDateTime time, Pageable pageable);
}
