package com.example.workoutmate.domain.user.dto;

import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.enums.UserGender;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserEditResponseDto {

    private final Long id;
    private final String email;
    private final String name;
    private final UserGender gender;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public UserEditResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.gender = user.getGender();
        this.createdAt = user.getCreatedAt();
        this.modifiedAt = user.getModifiedAt();
    }
}
