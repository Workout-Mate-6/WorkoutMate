package com.example.workoutmate.domain.recommend.v3.dto;

import com.example.workoutmate.domain.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

