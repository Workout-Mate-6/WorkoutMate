package com.example.workoutmate.domain.comment.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.service.BoardSearchService;
import com.example.workoutmate.domain.comment.dto.CommentRequestDto;
import com.example.workoutmate.domain.comment.dto.CommentResponseDto;
import com.example.workoutmate.domain.comment.entity.Comment;
import com.example.workoutmate.domain.comment.entity.CommentMapper;
import com.example.workoutmate.domain.comment.repository.CommentRepository;
import com.example.workoutmate.domain.notification.enums.NotificationType;
import com.example.workoutmate.domain.notification.service.NotificationService;
import com.example.workoutmate.domain.participation.service.ParticipationCreateService;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.workoutmate.global.enums.CustomErrorCode.*;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BoardSearchService boardSearchService;
    private final ParticipationCreateService participationCreateService;
    private final NotificationService notificationService;

    @Transactional
    public CommentResponseDto createComment(Long boardId, CommentRequestDto requestDto, CustomUserPrincipal authUser) {
        Board board = boardSearchService.getBoardById(boardId);
        User user = userService.findById(authUser.getId());

        // Mapper클래스로 DTO를 엔티티로 변환
        Comment comment = CommentMapper.commentRequestToComment(requestDto, board, user);
        Comment savedComment = commentRepository.save(comment);

        // participation 구현중에 로직 추가했습니다.!
//        participationCreateService.participationInjector(board, user);

        // 본인 게시글이 아닐 경우에 알림 전송
        if (!board.getWriter().getId().equals(user.getId())) {
            notificationService.sendNotification(
                    board.getWriter(),
                    NotificationType.COMMENT,
                    String.format("%s님이 '%s' 게시글에 댓글을 작성했습니다: \"%s\"",
                            user.getName(), board.getTitle(), requestDto.getContent())
            );
        }

        return CommentMapper.data(savedComment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getComment(Long boardId, Pageable pageable) {
        Board board = boardSearchService.getBoardById(boardId);

        // 레포지토리에서 게시글 ID로 댓글 목록 조회 후, DTO로 변환
        Page<Comment> comments = commentRepository.findAllByBoardId(boardId, pageable);

        return comments.map(CommentMapper::data);
    }

    @Transactional
    public CommentResponseDto updateComment(Long boardId, Long commentId, CommentRequestDto requestDto, CustomUserPrincipal authUser) {
        Board board = boardSearchService.getBoardById(boardId);
        Comment comment = findById(commentId);

        // 해당 댓글이 요청받은 board에 속한 댓글인지 검증
        if (!comment.getBoard().getId().equals(board.getId())) {
            throw new CustomException(COMMENT_NOT_IN_BOARD);
        }

        User user = userService.findById(authUser.getId());

        // 댓글 작성자와 현재 로그인한 사용자가 일치하는지 검증
        validateCommentWriter(comment, user);

        comment.updateComment(requestDto.getContent());

        commentRepository.flush();

        return CommentMapper.data(comment);
    }


    @Transactional
    public void deleteComment(Long boardId, Long commentId, CustomUserPrincipal authUser) {
        Board board = boardSearchService.getBoardById(boardId);
        Comment comment = findById(commentId);

        // 해당 댓글이 요청받은 board에 속한 댓글인지 검증
        if (!comment.getBoard().getId().equals(board.getId())) {
            throw new CustomException(COMMENT_NOT_IN_BOARD);
        }

        User user = userService.findById(authUser.getId());

        // 댓글 작성자와 현재 로그인한 사용자가 일치하는지 검증
        validateCommentWriter(comment, user);

        commentRepository.delete(comment);
    }

    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
    }

    // 댓글 작성자와 현재 로그인한 사용자가 일치하는지 검증
    public void validateCommentWriter(Comment comment, User user) {
        if (!comment.getWriter().getId().equals(user.getId())) {
            throw new CustomException(UNAUTHORIZED_COMMENT_ACCESS);
        }
    }
}
