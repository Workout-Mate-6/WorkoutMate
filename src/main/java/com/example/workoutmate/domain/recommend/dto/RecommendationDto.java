package com.example.workoutmate.domain.recommend.dto;

import com.example.workoutmate.domain.board.entity.Board;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendationDto {

    private BoardResponseDto board;
    private double finalScore; // 최종 점수

    // 요소별 점수
    private double participationScore;
    private double zzimScore;
    private double friendScore;
    private double typeScore;
    private double timeScore;
}
