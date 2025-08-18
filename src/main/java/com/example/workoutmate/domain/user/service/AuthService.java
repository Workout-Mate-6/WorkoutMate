package com.example.workoutmate.domain.user.service;

import com.example.workoutmate.domain.user.dto.*;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.entity.UserMapper;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.global.util.JwtUtil;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional
    public AuthResponseDto signup(SignupRequestDto signupRequestDto) {

        if (userRepository.existsByEmailAndIsDeletedTrue(signupRequestDto.getEmail())){
            throw new CustomException(CustomErrorCode.ALREADY_WITHDRAWN_EMAIL);
        }

        // 인증 완료된 사용자 email 중복 확인
        if (userRepository.existsByEmailAndIsDeletedFalseAndIsEmailVerifiedTrue(signupRequestDto.getEmail())) {
            throw new CustomException(CustomErrorCode.DUPLICATE_EMAIL);
        }

        // 미인증 계정 → 안내만
        Optional<User> unverifiedUser = userRepository.findByEmailAndIsDeletedFalseAndIsEmailVerifiedFalse(signupRequestDto.getEmail());
        if (unverifiedUser.isPresent()) {
            throw new CustomException(CustomErrorCode.EMAIL_NOT_VERIFIED_FOR_SIGNUP);
        }

        // 신규 회원가입
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        User user = UserMapper.signupRequestToUser(signupRequestDto, encodedPassword);

        userRepository.save(user);
        emailVerificationService.sendVerificationCode(user);

        return UserMapper.data(user);
    }

    @Transactional
    public EmailVerificationResponseDto verifyEmail(EmailVerificationRequestDto requestDto) {

        // 이메일로 미인증 된 사용자 찾기
        User user = userRepository.findByEmailAndIsDeletedFalseAndIsEmailVerifiedFalse(requestDto.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "가입 대기 중인 사용자를 찾을 수 없습니다."));

        // 인증코드 일치 + 만료 된 코드 확인
        emailVerificationService.validateVerificationCode(user, requestDto.getCode());
        emailVerificationService.completeVerification(user);

        return UserMapper.toVerificationResponse(user);
    }

    @Transactional
    public void resendEmail(ResendEmailRequestDto resendEmailRequestDto) {

        User user = userRepository.findByEmailAndIsDeletedFalseAndIsEmailVerifiedFalse(resendEmailRequestDto.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new CustomException(CustomErrorCode.ALREADY_VERIFIED);
        }

        emailVerificationService.sendVerificationCode(user);

    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // 존재하는 유저인지 확인
        User user = userRepository.findByEmailAndIsDeletedFalse(loginRequestDto.getEmail()).orElseThrow(
                () -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 이메일 인증 된 유저인지 확인
        if (!user.isEmailVerified()) {
            throw new CustomException(CustomErrorCode.EMAIL_NOT_VERIFIED_FOR_LOGIN);
        }

        // 비밀번호 체크
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new CustomException(CustomErrorCode.PASSWORD_NOT_MATCHED);
        }

        // 토큰 발급 및 저장
        return generateAndSaveToken(user);
    }

    @Transactional
    public int findUnverifiedUsersAndDelete(LocalDateTime time, int batchSize) {
        Pageable pageable = PageRequest.of(0, batchSize);
        Page<User> page = userRepository.findUnverifiedUsers(time, pageable);
        List<User> users = page.getContent();

        if (!users.isEmpty()) {
            userRepository.deleteAllInBatch(users);
        }
        return users.size();
    }

    // 토큰 재발급
    public LoginResponseDto refreshAccessToken(String refreshToken) {
        // Refresh Token 유효성 검증
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new CustomException(CustomErrorCode.INVALID_REFRESH_TOKEN);
        }

        // refresh token 의 jti 로 userId 찾아
        String jti = jwtUtil.getJti(refreshToken);
        Long userId = refreshTokenService.getUserIdByJti(jti);

        User user = userRepository.findByIdAndIsDeletedFalseAndIsEmailVerifiedTrue(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 기존 refresh 토큰 Redis 에서 삭제
        refreshTokenService.deleteRefreshTokenJti(jti);

        // 토큰 발급 및 저장
        return generateAndSaveToken(user);
    }

    // 토큰 발급 및 저장 메서드
    private LoginResponseDto generateAndSaveToken(User user) {
        // Access token 발급
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        // Refresh token 발급
        String refreshToken = jwtUtil.createRefreshToken(user.getId());
        // redis 에 refresh token 의 jti 저장
        refreshTokenService.saveRefreshTokenJti(jwtUtil.getJti(refreshToken), user.getId());

        return new LoginResponseDto(accessToken, refreshToken);
    }

    // logout
    public void logout(String bearerAccessToken, String refreshToken) {
        // accessToken 에서 Jti 추출
        String accessToken = jwtUtil.substringToken(bearerAccessToken);
        String accessTokenJti = jwtUtil.getJti(accessToken);

        // accessToken Jti를 블랙리스트에 추가 (만료시간까지 저장)
        Date expiration = jwtUtil.getExpiration(accessToken);
        Duration remainingMillis = Duration.ofMillis(expiration.getTime() - System.currentTimeMillis());
        tokenBlacklistService.addToBlacklist(accessTokenJti, remainingMillis);

        // refreshToken 에서 refreshTokenJti 받아와서 저장된 refreshTokenJti 삭제
        String refreshTokenJti = jwtUtil.getJti(refreshToken);
        refreshTokenService.deleteRefreshTokenJti(refreshTokenJti);
    }
}
