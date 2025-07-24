package com.example.workoutmate.domain.board.dto;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {

    private final Long id;
    private final Long writerId;
    private final String title;
    private final String content;
    private final SportType sportType;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.writerId = board.getWriter().getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.sportType = board.getSportType();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
    }
}
