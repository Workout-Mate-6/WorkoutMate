package com.example.workoutmate.domain.user.dto;

import com.example.workoutmate.domain.user.constraint.ValidPassword;
import com.example.workoutmate.domain.user.enums.UserGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank
    @ValidPassword
    private String password;

    @NotBlank
    @Size(max = 20, message = "이름은 최대 20자까지 입력이 가능합니다.")
    private String name;

    @NotNull
    private UserGender gender;
}
