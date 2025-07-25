package com.example.workoutmate.domain.board.entity;

import com.example.workoutmate.domain.board.dto.BoardRequestDto;
import com.example.workoutmate.domain.user.entity.User;

public class BoardMapper {

    // dto -> Entity
    public static Board boardRequestToBoard(BoardRequestDto dto, User user){
        return Board.builder()
                .writer(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .sportType(dto.getSportType())
                .targetCount(dto.getTargetCount())
                .build();
    }
}
