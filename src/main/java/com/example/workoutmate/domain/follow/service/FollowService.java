package com.example.workoutmate.domain.follow.service;

import com.example.workoutmate.domain.follow.entity.Follow;
import com.example.workoutmate.domain.follow.repository.FollowRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.rmi.AlreadyBoundException;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;


    public void follow(Long userId, long l) {
        // 본인 팔로워 못하게
        if (userId.equals(l)) {
            throw new CustomException(CustomErrorCode.CANNOT_FOLLOW_SELF);
        }

        // 사용자 조회
        User follower = userService.findById(userId);
        User following = userService.findById(l);

        // 팔로워 중인지 아닌지 검증
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new CustomException(CustomErrorCode.ALLREADY_FOLLOWING);
        }

        // 팔로우 등록
        Follow follow = new Follow(follower, following);
        followRepository.save(follow);
    }
}
