package com.example.workoutmate.domain.comment.entity;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.comment.dto.CommentRequestDto;
import com.example.workoutmate.domain.comment.dto.CommentResponseDto;
import com.example.workoutmate.domain.user.entity.User;

public class CommentMapper {

    // Dto -> Entity
    public static Comment commentRequestToComment(CommentRequestDto dto, Board board, User user){
        return Comment.builder()
                .board(board)
                .writer(user)
                .content(dto.getContent())
                .build();
    }

    // Entity -> Dto
    public static CommentResponseDto data(Comment comment){
        return CommentResponseDto.builder()
                .id(comment.getId())
                .boardId(comment.getBoard().getId())
                .userId(comment.getWriter().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }

}
