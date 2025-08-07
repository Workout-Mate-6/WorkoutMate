package com.example.workoutmate.domain.recommend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSimpleDto {

    private Long id;
    private String nickname;
}
