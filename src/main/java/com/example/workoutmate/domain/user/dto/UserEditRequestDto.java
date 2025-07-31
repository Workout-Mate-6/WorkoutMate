package com.example.workoutmate.domain.user.dto;

import com.example.workoutmate.domain.user.constraint.ValidPassword;
import lombok.Getter;

@Getter
public class UserEditRequestDto {

    @ValidPassword
    private String password;
    @ValidPassword
    private String passwordCheck;
    private String name;
    private String gender;
}
