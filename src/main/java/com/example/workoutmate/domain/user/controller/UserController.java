package com.example.workoutmate.domain.user.controller;

import com.example.workoutmate.domain.user.dto.UserEditRequestDto;
import com.example.workoutmate.domain.user.dto.UserEditResponseDto;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/users/me")
    public ResponseEntity<ApiResponse<UserEditResponseDto>> editUserInfo(
            @Valid @RequestBody UserEditRequestDto requestDto,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {

        UserEditResponseDto userEditResponseDto = userService.editUserInfo(authUser, requestDto);

        return ApiResponse.success(HttpStatus.OK, "회원 정보 수정이 완료되었습니다.", userEditResponseDto);

    }

}
