package com.example.workoutmate.domain.user.service;

import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.workoutmate.global.enums.CustomErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;




    /* 도메인 관련 메서드 */

    public User findById(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
    }
}
