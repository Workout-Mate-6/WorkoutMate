package com.example.workoutmate.domain.user.dto;

import com.example.workoutmate.domain.user.enums.UserGender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserInfoResponseDto {

    private Long id;
    private String email;
    private String name;
    private UserGender gender;
    private int followerCount;
    private int followingCount;
    private int myBoardCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
