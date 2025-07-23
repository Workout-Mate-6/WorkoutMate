package com.example.workoutmate.domain.follow.service;

import com.example.workoutmate.domain.follow.dto.FollowsResponseDto;
import com.example.workoutmate.domain.follow.entity.Follow;
import com.example.workoutmate.domain.follow.repository.FollowRepository;
import com.example.workoutmate.domain.follow.repository.QFollowsRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final QFollowsRepository qFollowsRepository;
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

    public List<FollowsResponseDto> viewFollower(
            Long userId, Integer size, Long cursor
    ) {
        return qFollowsRepository.viewFollower(userId, size, cursor);
    }

    public List<FollowsResponseDto> viewFollowing(Long userId, Integer size, Long cursor) {
        return qFollowsRepository.viewFollowing(userId, size, cursor);
    }

    // 게시글 쪽에서 사용하는 메서드
    public List<Long> getFollowingUserIds(Long userId) {
        List<Follow> followList = followRepository.findAllByFollowerId(userId);

        if (followList.isEmpty()) {
            throw new CustomException(CustomErrorCode.EMPTY_FOLLOWING_LIST);
        }

        return followList.stream()
                .map(follow -> follow.getFollowing().getId())
                .toList();
    }

    @Transactional
    public void unfollow(Long userId, CustomUserPrincipal authUser) {

        // 본인 언팔로우 못하게
        if (userId.equals(authUser.getId())) {
            throw new CustomException(CustomErrorCode.CANNOT_UNFOLLOW_SELF);
        }

        // 두번 언팔되는거 방지
        boolean exists = followRepository.existsByFollowerIdAndFollowingId(authUser.getId(), userId);
        if (!exists) {
            throw new CustomException(CustomErrorCode.NOT_FOLLOWING);
        }

        // 언팔로우 하면 하드 딜리트
        followRepository.deleteByfollowerIdAndFollowingId(authUser.getId(), userId);
    }
}
