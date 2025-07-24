package com.example.workoutmate.domain.user.repository;

import com.example.workoutmate.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailAndIsDeletedFalse(String email);
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByIdAndIsDeletedFalse(Long id);


    boolean existsByEmailAndIsDeletedFalseAndEmailVerifiedTrue(String email);

    Optional<User> findByEmailAndIsDeletedFalseAndEmailVerifiedFalse(String email);
}
