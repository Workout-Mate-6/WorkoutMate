package com.example.workoutmate.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailVerificationRequestDto {

    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank
    private String code;
}
