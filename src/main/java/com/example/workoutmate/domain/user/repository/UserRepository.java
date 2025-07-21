package com.example.workoutmate.domain.user.repository;

import com.example.workoutmate.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailAndIsDeletedFalse(String email);
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByIdAndIsDeletedFalse(Long id);

}