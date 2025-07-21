package com.example.workoutmate.domain.follow.service;

import com.example.workoutmate.domain.follow.repository.FollowRepository;
import com.example.workoutmate.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;


    @Transactional
    public void follow(Long userId) {

    }
}
