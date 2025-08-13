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


    /** User 엔티티에서 필요한 필드만 골라서 만든다. */
    public static UserSimpleDto from(User user) {
        if (user == null) return null;
        return UserSimpleDto.builder()
                .id(user.getId())
                .nickname(user.getName())
                .build();
    }
}
