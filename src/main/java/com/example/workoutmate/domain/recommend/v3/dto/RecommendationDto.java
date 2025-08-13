package com.example.workoutmate.domain.recommend.v3.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDto {
    private Long boardId;
    private int matchPercent; // 0~100
    private List<String> reasons; // 간단 설명
    private BoardResponseDto board;
}
