package com.example.workoutmate.domain.user.enums;

import com.example.workoutmate.global.exception.CustomException;

import java.util.Arrays;

import static com.example.workoutmate.global.enums.CustomErrorCode.INVALID_REQUEST;

public enum Role {
    ADMIN, USER;

    public Role of(String role) {
        return Arrays.stream(Role.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new CustomException(INVALID_REQUEST, "유효하지 않은 권한"));
    }
}
