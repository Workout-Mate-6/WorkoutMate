package com.example.workoutmate.domain.recommend.v3.dto;

import com.example.workoutmate.domain.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * 추천 응답 안에 중첩으로 들어가는 게시글 요약 정보.
 * Board 엔티티에서 API 응답에 꼭 필요한 최소 필드만 노출.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDto {

    private String title;
    private String content;
    private Long maxParticipants;
    private Long currentParticipants;
    private UserSimpleDto writer;


    /**
     * Board 엔티티 → BoardResponseDto 로의 정적 변환 헬퍼.
     * Service 쪽에서 간단하게 b -> toBoardDto(b)로 사용.
     */
    public static BoardResponseDto from(Board b) {
        return BoardResponseDto.builder()
                .title(b.getTitle())
                .content(b.getContent())
                .maxParticipants(b.getMaxParticipants() == null ? null : b.getMaxParticipants().longValue())
                .currentParticipants(b.getCurrentParticipants() == null ? null : b.getCurrentParticipants().longValue())
                .writer(UserSimpleDto.from(b.getWriter())) // 프로젝트 방식에 맞춰 변환
                .build();
    }
}

