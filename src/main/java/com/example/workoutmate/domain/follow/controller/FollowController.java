package com.example.workoutmate.domain.follow.controller;

import com.example.workoutmate.domain.follow.dto.FollowsResponseDto;
import com.example.workoutmate.domain.follow.service.FollowService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class FollowController {

    private final FollowService followService;

    // 팔로우 걸기
    @PostMapping("/follows/{userId}")
    public ResponseEntity<ApiResponse<Void>> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ) {
        followService.follow(userId, authUser);
        return ApiResponse.success(HttpStatus.OK, "팔로우 하였습니다.", null);
    }

    // 나를 팔로우한 사람들 조회
    @GetMapping("/follows/follower/{userId}")
    public ResponseEntity<ApiResponse<List<FollowsResponseDto>>> follower(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long cursor
    ) {
        return ApiResponse.success(HttpStatus.OK, "팔로워 목록을 조회하였습니다.", followService.viewFollower(userId, size, cursor));
    }

    // 내가 팔로우한 사람들 조회
    @GetMapping("/follows/following/{userId}")
    public ResponseEntity<ApiResponse<List<FollowsResponseDto>>> following(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long cursor
    ) {
        return ApiResponse.success(HttpStatus.OK, "팔로잉 목록을 조회하였습니다.", followService.viewFollowing(userId, size, cursor));
    }

    // 팔로우 취소
    @DeleteMapping("/follows/following/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ) {
        followService.unfollow(userId, authUser);
        return ApiResponse.success(HttpStatus.OK, "언팔로우 되었습니다.", null);
    }
}
