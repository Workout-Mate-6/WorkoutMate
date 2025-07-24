package com.example.workoutmate.domain.user.dto;

import lombok.Getter;

@Getter
public class EmailVerificationRequestDto {
    private String email;
    private String code;
}
