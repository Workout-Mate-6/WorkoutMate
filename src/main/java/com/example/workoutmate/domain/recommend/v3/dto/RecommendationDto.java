package com.example.workoutmate.domain.recommend.v3.dto;

import lombok.*;

import java.util.List;


/**
 * - boardId: 추천된 게시글의 ID (중복 방지를 위한 식별자)
 *  - matchPercent: 사용자-게시글 매칭 점수(0~100, 반올림)
 *  - reasons: 추천 사유(간단 문구 리스트)
 *  - board: 게시글 요약 정보(제목/내용/인원/작성자 등)
 */
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
