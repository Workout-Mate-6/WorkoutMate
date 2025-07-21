package com.example.workoutmate.domain.follow.controller;

import com.example.workoutmate.domain.follow.service.FollowService;
import com.example.workoutmate.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follows/{userId}")
    public ResponseEntity<ApiResponse<Void>> followUser(
            @PathVariable Long userId
    ) {
        followService.follow(userId, 1L);
        return ApiResponse.success(HttpStatus.OK, "팔로우 하였습니다.", null);
    }
}
