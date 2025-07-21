package com.example.workoutmate.domain.user.dto;

import lombok.Getter;

@Getter
public class UserEditRequestDto {

    private String email;
    private String password;
    private String passwordCheck;
    private String name;
    private String gender;
}
