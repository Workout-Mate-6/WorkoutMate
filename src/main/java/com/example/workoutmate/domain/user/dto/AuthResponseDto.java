package com.example.workoutmate.domain.user.dto;

import com.example.workoutmate.domain.user.enums.UserGender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AuthResponseDto {
    private Long id;
    private String email;
    private String name;
    private UserGender gender;
    private LocalDateTime createdAt;
}
