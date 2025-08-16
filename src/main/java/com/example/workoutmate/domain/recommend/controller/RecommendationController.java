package com.example.workoutmate.domain.recommend.controller;

import com.example.workoutmate.domain.recommend.v3.dto.RecommendationDto;
import com.example.workoutmate.domain.recommend.v3.service.RecommendationServiceRebulid;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class RecommendationController {

    private final RecommendationServiceRebulid recommendationService;

    @GetMapping("/recommendation")
    public ResponseEntity<ApiResponse<List<RecommendationDto>>> recommendation(
            @AuthenticationPrincipal CustomUserPrincipal AuthUser,
            @RequestParam(defaultValue = "10") int limit) {

        return ApiResponse.success(HttpStatus.OK,"조회되었습니다.",
                recommendationService.getRecommendations(AuthUser.getId(), limit));
    }
}
