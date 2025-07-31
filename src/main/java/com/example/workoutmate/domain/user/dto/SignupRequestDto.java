package com.example.workoutmate.domain.user.dto;

import com.example.workoutmate.domain.user.constraint.ValidPassword;
import com.example.workoutmate.domain.user.enums.UserGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    private String name;

    @NotNull(message = "성별이 Null 일 수 는 없습니다!")
    @Pattern(regexp = "Male|Female", message = "올바른 성별을 입력해주세요.")
    private String gender;
}
