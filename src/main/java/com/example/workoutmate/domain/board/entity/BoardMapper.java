package com.example.workoutmate.domain.board.entity;

import com.example.workoutmate.domain.board.dto.BoardRequestDto;
import com.example.workoutmate.domain.board.dto.BoardResponseDto;
import com.example.workoutmate.domain.user.entity.User;

public class BoardMapper {

    // dto -> Entity
    public static Board boardRequestToBoard(BoardRequestDto dto, User user){
        return Board.builder()
                .writer(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .sportType(dto.getSportType())
                .maxParticipants(dto.getMaxParticipants())
                .build();
    }

    // Entity -> dto
    public static BoardResponseDto boardToBoardResponse(Board board) {
        return BoardResponseDto.builder()
                .id(board.getId())
                .writerId(board.getWriter().getId())
                .title(board.getTitle())
                .content(board.getContent())
                .sportType(board.getSportType())
                .maxParticipants(board.getMaxParticipants())
                .currentParticipants(board.getCurrentParticipants())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .modifiedAt(board.getModifiedAt())
                .build();
    }
}
