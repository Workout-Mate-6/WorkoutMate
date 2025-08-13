package com.example.workoutmate.domain.recommend.v3.dto;

import com.example.workoutmate.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSimpleDto {

    private Long id;
    private String nickname;

    public static UserSimpleDto from(User user) {
        if (user == null) return null;
        return UserSimpleDto.builder()
                .id(user.getId())
                .nickname(user.getName())
                .build();
    }
}
