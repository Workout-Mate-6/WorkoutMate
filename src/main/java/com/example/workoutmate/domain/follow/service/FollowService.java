package com.example.workoutmate.domain.follow.service;

import com.example.workoutmate.domain.follow.entity.Follow;
import com.example.workoutmate.domain.follow.repository.FollowRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;


    public void follow(Long userId, CustomUserPrincipal authUser) {
        // 본인 팔로워 못하게
        if (userId.equals(authUser.getId())) {
            throw new CustomException(CustomErrorCode.CANNOT_FOLLOW_SELF);
        }

        // 사용자 조회
        User follower = userService.findById(authUser.getId()); // 팔로우 하는 사람
        User following = userService.findById(userId); // 팔로우 당하는 사람

        // 팔로워 중인지 아닌지 검증
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new CustomException(CustomErrorCode.ALLREADY_FOLLOWING);
        }

        // 팔로우 등록
        Follow follow = new Follow(follower, following);
        followRepository.save(follow);
    }
}
