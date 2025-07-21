package com.example.workoutmate.domain.user.service;

import com.example.workoutmate.domain.user.dto.AuthResponseDto;
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
        if(userRepository.existsByEmail(signupRequestDto.getEmail())){
            throw new CustomException(CustomErrorCode.DUPLICATE_EMAIL);}

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        // Dto -> Entity
        User user = UserMapper.signupRequestToUser(signupRequestDto, encodedPassword);

        User savedUser = userRepository.save(user);

        return UserMapper.data(savedUser);
    }

}
