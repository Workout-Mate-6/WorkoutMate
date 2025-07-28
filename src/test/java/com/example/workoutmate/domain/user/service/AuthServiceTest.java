package com.example.workoutmate.domain.user.service;

import com.example.workoutmate.domain.user.dto.*;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.enums.UserGender;
import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.global.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    void 정상_회원가입() {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("test@example.com", "Password!123", "테스트 유저", UserGender.Male);

        when(userRepository.existsByEmailAndIsDeletedFalseAndIsEmailVerifiedTrue(anyString())).thenReturn(false);
        when(userRepository.findByEmailAndIsDeletedFalseAndIsEmailVerifiedFalse(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPW");

        User user = User.builder()
                .email("test@example.com")
                .password("Password!123")
                .name("테스트 유저")
                .gender(UserGender.Male)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        AuthResponseDto response = authService.signup(signupRequestDto);

        // then
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        verify(passwordEncoder, times(1)).encode("Password!123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailVerificationService, times(1)).sendVerificationCode(any(User.class));

    }

    @Test
    void 정상_이메일_인증() {
        // given
        EmailVerificationRequestDto emailVerificationRequestDto = new EmailVerificationRequestDto("test@example.com", "123456");

        User user = User.builder()
                .email("test@example.com")
                .verificationCode("123456")
                .build();

        when(userRepository.findByEmailAndIsDeletedFalseAndIsEmailVerifiedFalse(anyString())).thenReturn(Optional.of(user));
        doNothing().when(emailVerificationService).validateVerificationCode(user, "123456");
        doNothing().when(emailVerificationService).completeVerification(user);

        // when
        EmailVerificationResponseDto response = authService.verifyEmail(emailVerificationRequestDto);

        // then
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        verify(emailVerificationService, times(1)).validateVerificationCode(user, "123456");
        verify(emailVerificationService, times(1)).completeVerification(user);
    }

    @Test
    void 정상_이메일_코드_재전송() {
        // given
        ResendEmailRequestDto resendEmailRequestDto = new ResendEmailRequestDto("test@example.com");

        User user = User.builder()
                .email("test@example.com")
                .build();

        when(userRepository.findByEmailAndIsDeletedFalseAndIsEmailVerifiedFalse(anyString())).thenReturn(Optional.of(user));
        doNothing().when(emailVerificationService).sendVerificationCode(user);

        // when
        authService.resendEmail(resendEmailRequestDto);

        // then
        verify(emailVerificationService, times(1)).sendVerificationCode(user);

    }

    @Test
    void 정상_로그인() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "Password!123");

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPW")
                .isEmailVerified(true)
                .role(UserRole.GUEST)
                .build();

        when(userRepository.findByEmailAndIsDeletedFalse(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password!123", "encodedPW")).thenReturn(true);
        when(jwtUtil.createToken(user.getId(), user.getEmail(), user.getRole())).thenReturn("jwt.token");

        // when
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);

        // then
        assertThat(loginResponseDto.getToken()).isEqualTo("jwt.token");

    }

    @Test
    void 정상_배치_삭제() {
        // given
        List<User> users = List.of(
                User.builder().email("a@test.com").build(),
                User.builder().email("b@test.com").build()
        );
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findUnverifiedUsers(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(page);

        // when
        int result = authService.findUnverifiedUsersAndDelete(LocalDateTime.now(), 1000);

        // then
        assertThat(result).isEqualTo(2);
        verify(userRepository).deleteAllInBatch(users);
    }
}