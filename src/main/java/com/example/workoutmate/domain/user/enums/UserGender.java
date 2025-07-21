package com.example.workoutmate.domain.user.enums;

import com.example.workoutmate.global.exception.CustomException;

import java.util.Arrays;

import static com.example.workoutmate.global.enums.CustomErrorCode.GENDER_NOT_FOUND;

public enum UserGender {
    Male, Female;

    public static UserGender from(String value) {
        return Arrays.stream(values())
                .filter(g -> g.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new CustomException(GENDER_NOT_FOUND, GENDER_NOT_FOUND.getMessage()));
    }
}
