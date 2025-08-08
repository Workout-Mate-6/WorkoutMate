package com.example.workoutmate.domain.user.controller;

import com.example.workoutmate.domain.user.dto.*;
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
        return ApiResponse.success(HttpStatus.OK, "회원가입을 성공했습니다. 이메일 인증을 해주세요.", signup);
    }

    @PostMapping("/auth/signup/verify")
    public ResponseEntity<ApiResponse<EmailVerificationResponseDto>> verifyEmail(@Valid @RequestBody EmailVerificationRequestDto emailVerificationRequestDto) {
        EmailVerificationResponseDto verifyEmail = authService.verifyEmail(emailVerificationRequestDto);
        return ApiResponse.success(HttpStatus.OK, "이메일 인증이 완료되었습니다.", verifyEmail);
    }

    @PostMapping("/auth/signup/resend")
    public ResponseEntity<ApiResponse<Void>> resendEmail(@Valid @RequestBody ResendEmailRequestDto resendEmailRequestDto){
        authService.resendEmail(resendEmailRequestDto);
        return ApiResponse.success(HttpStatus.OK, "이메일 인증 코드가 재발송되었습니다.", null);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto login = authService.login(loginRequestDto);
        return ApiResponse.success(HttpStatus.OK, "로그인이 완료되었습니다.", login);
    }

}
