package com.example.workoutmate.domain.user.entity;

import com.example.workoutmate.domain.user.dto.AuthResponseDto;
import com.example.workoutmate.domain.user.dto.EmailVerificationResponseDto;
import com.example.workoutmate.domain.user.dto.SignupRequestDto;
import com.example.workoutmate.domain.user.enums.UserRole;

public class UserMapper {

    // Dto -> Entity
    public static User signupRequestToUser(SignupRequestDto dto, String encodedPassword){
        return User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .name(dto.getName())
                .gender(dto.getGender())
                .role(UserRole.GUEST)
                .isDeleted(false)
                .build();
    }

    // Entity -> AuthResponseDto
    public static AuthResponseDto data(User user){
        return AuthResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // Entity -> EmailVerificationResponseDto
    public static EmailVerificationResponseDto toVerificationResponse(User user){
        return EmailVerificationResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .gender(user.getGender())
                .isEmailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
