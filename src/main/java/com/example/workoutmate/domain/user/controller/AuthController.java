package com.example.workoutmate.domain.user.controller;

import com.example.workoutmate.domain.user.dto.AuthResponseDto;
import com.example.workoutmate.domain.user.dto.LoginRequestDto;
import com.example.workoutmate.domain.user.dto.LoginResponseDto;
import com.example.workoutmate.domain.user.dto.SignupRequestDto;
import com.example.workoutmate.domain.user.service.AuthService;
import com.example.workoutmate.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<AuthResponseDto>> signup(@Valid @RequestBody SignupRequestDto signupRequestDto){
        AuthResponseDto signup = authService.signup(signupRequestDto);
        return ApiResponse.success(HttpStatus.OK, "회원가입이 성공했습니다.", signup);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto login = authService.login(loginRequestDto);
        return ApiResponse.success(HttpStatus.OK, "로그인이 완료되었습니다.", login);
    }

}
