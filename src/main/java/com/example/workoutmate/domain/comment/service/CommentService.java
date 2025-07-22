package com.example.workoutmate.domain.comment.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.service.BoardService;
import com.example.workoutmate.domain.comment.dto.CommentRequestDto;
import com.example.workoutmate.domain.comment.dto.CommentResponseDto;
import com.example.workoutmate.domain.comment.entity.Comment;
import com.example.workoutmate.domain.comment.entity.CommentMapper;
import com.example.workoutmate.domain.comment.repository.CommentRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BoardService boardService;

    @Transactional
    public CommentResponseDto createComment(Long boardId, CommentRequestDto requestDto, CustomUserPrincipal authUser) {
        Board board = boardService.getBoardById(boardId);
        User user = userService.findById(authUser.getId());

        // Mapper클래스로 DTO를 엔티티로 변환
        Comment comment = CommentMapper.commentRequestToComment(requestDto, board, user);

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.data(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComment(Long boardId) {

        List<CommentResponseDto> comments = commentRepository.findAllByBoardId(boardId)
                .stream().map(CommentMapper::data).toList();

        return comments;
    }
}
