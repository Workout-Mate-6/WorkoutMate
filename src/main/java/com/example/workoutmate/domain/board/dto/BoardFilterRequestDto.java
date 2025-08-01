package com.example.workoutmate.domain.board.dto;

import com.example.workoutmate.domain.board.entity.SportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class BoardFilterRequestDto {

    private Boolean onlyMyPosts;
    private Boolean onlyFollowing;
    private SportType sportType;
    private Boolean onlyZzimmed;
}
