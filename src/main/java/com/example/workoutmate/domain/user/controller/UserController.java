package com.example.workoutmate.domain.user.controller;

import com.example.workoutmate.domain.user.dto.UserEditRequestDto;
import com.example.workoutmate.domain.user.dto.UserEditResponseDto;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private UserRepository userRepository;

    @PatchMapping("/users/me")
    public ResponseEntity<ApiResponse<UserEditResponseDto>> editUserInfo(
            @RequestBody UserEditRequestDto requestDto) {

        UserEditResponseDto userEditResponseDto = userService.editUserInfo(1L, requestDto);

        return ApiResponse.success(HttpStatus.OK, "회원 정보 수정이 완료되었습니다.", userEditResponseDto);

    }

}
