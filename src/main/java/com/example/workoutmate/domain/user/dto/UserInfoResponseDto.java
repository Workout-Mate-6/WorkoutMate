package com.example.workoutmate.domain.user.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserInfoResponseDto {

    private Long id;
    private String email;
    private String name;
    private String gender;
    private int followerCount;
    private int followingCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
