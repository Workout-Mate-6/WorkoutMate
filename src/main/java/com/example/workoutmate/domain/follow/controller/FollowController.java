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

    @PostMapping("/follows/{userId}")
    public ResponseEntity<ApiResponse<Void>> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ) {
        followService.follow(userId, authUser);
        return ApiResponse.success(HttpStatus.OK, "팔로우 하였습니다.", null);
    }

    @GetMapping("/follows/follower/{userId}")
    public ResponseEntity<ApiResponse<List<FollowsResponseDto>>> follower(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long cursor
    ) {
        return ApiResponse.success(HttpStatus.OK, "", followService.viewFollower(userId, size, cursor));
    }
}
