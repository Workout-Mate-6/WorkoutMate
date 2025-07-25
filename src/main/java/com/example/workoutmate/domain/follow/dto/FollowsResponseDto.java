package com.example.workoutmate.domain.follow.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class FollowsResponseDto {

    private final Long id;
    private final String username;

}
