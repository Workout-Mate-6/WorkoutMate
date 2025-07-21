package com.example.workoutmate.domain.user.service;

import com.example.workoutmate.domain.user.dto.AuthResponseDto;
import com.example.workoutmate.domain.user.dto.LoginRequestDto;
import com.example.workoutmate.domain.user.dto.LoginResponseDto;
import com.example.workoutmate.domain.user.dto.SignupRequestDto;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.entity.UserMapper;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.global.config.JwtUtil;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponseDto signup(SignupRequestDto signupRequestDto){
        // email 중복 확인
        if(userRepository.existsByEmailAndIsDeletedFalse(signupRequestDto.getEmail())){
            throw new CustomException(CustomErrorCode.DUPLICATE_EMAIL);}

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        // Dto -> Entity
        User user = UserMapper.signupRequestToUser(signupRequestDto, encodedPassword);

        User savedUser = userRepository.save(user);

        return UserMapper.data(savedUser);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        // 존재하는 유저인지 확인
        User user = userRepository.findByEmailAndIsDeletedFalse(loginRequestDto.getEmail()).orElseThrow(
                () -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 비밀번호 체크
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            throw new CustomException(CustomErrorCode.PASSWORD_NOT_MATCHED);
        }
        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getRole());

        return new LoginResponseDto(bearerToken);
    }
}
