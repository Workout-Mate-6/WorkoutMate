package com.example.workoutmate.domain.user.controller;

import com.example.workoutmate.domain.user.dto.*;
import com.example.workoutmate.domain.user.service.AuthService;
import com.example.workoutmate.global.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                                               HttpServletResponse response){
        LoginResponseDto login = authService.login(loginRequestDto);
        response.addHeader(HttpHeaders.AUTHORIZATION, login.getAccessToken());

        // refreshToken 을 HttpOnly 쿠키로 저장 (보안 강화)
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", login.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7*24*60*60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ApiResponse.success(HttpStatus.OK, "로그인이 완료되었습니다.", login);
    }

}
