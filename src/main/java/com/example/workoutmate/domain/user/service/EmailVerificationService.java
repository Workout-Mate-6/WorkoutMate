package com.example.workoutmate.domain.user.service;

import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import com.example.workoutmate.global.util.SendGridUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final SendGridUtil sendGridUtil;

    public void sendVerificationCode(User user) {
        // 6자리 랜덤 인증코드 생성
        String code = generateVerificationCode();

        // 인증코드와 만료시간 세팅 -> 5분
        user.issueVerificationCode(code, LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        // 이메일 발송
        try {
            sendGridUtil.sendEmailVerificationCode(user, code);
        } catch (IOException e) {
            throw new CustomException(CustomErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public void validateVerificationCode(User user, String inputCode) {
        // 인증코드 불일치
        if (!user.getVerificationCode().equals(inputCode)) {
            throw new CustomException(CustomErrorCode.EMAIL_INVALID_VERIFICATION_CODE);
        }

        // 인증 코드 만료 시
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(CustomErrorCode.VERIFICATION_CODE_EXPIRED);
        }
    }

    // 인증 성공 시
    public void completeVerification(User user) {
        user.completeEmailVerification();
        userRepository.save(user);
    }

    // 6자리 랜덤 인증코드 생성
    public String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900_000) + 100_000;
        return String.valueOf(code);
    }
}
