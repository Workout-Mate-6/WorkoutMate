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
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.workoutmate.global.enums.CustomErrorCode.COMMENT_NOT_IN_BOARD;
import static com.example.workoutmate.global.enums.CustomErrorCode.UNAUTHORIZED_COMMENT_ACCESS;


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

        // 레포지토리에서 게시글 ID로 댓글 목록 조회 후, DTO로 변환
        List<CommentResponseDto> comments = commentRepository.findAllByBoardId(boardId)
                .stream().map(CommentMapper::data).toList();

        return comments;
    }

    @Transactional
    public CommentResponseDto updateComment(Long boardId, Long commentId, CommentRequestDto requestDto, CustomUserPrincipal authUser) {
        Board board = boardService.getBoardById(boardId);
        Comment comment = findById(commentId);

        if (!comment.getBoard().getId().equals(board.getId())){
            throw new CustomException(COMMENT_NOT_IN_BOARD, COMMENT_NOT_IN_BOARD.getMessage());
        }

        User user = userService.findById(authUser.getId());

        if (!comment.getWriter().getId().equals(user.getId())) {
            throw new CustomException(UNAUTHORIZED_COMMENT_ACCESS);
        }

        comment.updateComment(requestDto.getContent());

        commentRepository.flush();

        return CommentMapper.data(comment);
    }

    public Comment findById(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));
    }
}
