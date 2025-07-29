package com.example.workoutmate.domain.comment.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.service.BoardSearchService;
import com.example.workoutmate.domain.comment.dto.CommentRequestDto;
import com.example.workoutmate.domain.comment.dto.CommentResponseDto;
import com.example.workoutmate.domain.comment.entity.Comment;
import com.example.workoutmate.domain.comment.repository.CommentRepository;
import com.example.workoutmate.domain.participation.service.ParticipationCreateService;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private BoardSearchService boardSearchService;

    @Mock
    private ParticipationCreateService participationCreateService;

    @Test
    void 정상_댓글생성() {
        // given
        Long boardId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto("테스트 댓글 내용");
        CustomUserPrincipal authUser = new CustomUserPrincipal(2L, "test@example.com", UserRole.GUEST);

        Board board = Board.builder()
                .id(boardId)
                .build();

        User user = User.builder()
                .id(authUser.getId())
                .build();

        Comment comment = Comment.builder()
                .id(100L)
                .board(board)
                .writer(user)
                .content("테스트 댓글 내용")
                .build();

        when(boardSearchService.getBoardById(boardId)).thenReturn(board);
        when(userService.findById(authUser.getId())).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // when
        CommentResponseDto response = commentService.createComment(boardId, requestDto, authUser);

        // then
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getBoardId()).isEqualTo(boardId);
        assertThat(response.getUserId()).isEqualTo(authUser.getId());
        assertThat(response.getContent()).isEqualTo("테스트 댓글 내용");

        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(participationCreateService).participationInjector(eq(board), eq(user), any(Comment.class));
    }

    @Test
    void 정상_댓글조회() {
        // given
        Long boardId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Comment comment1 = Comment.builder()
                .id(1L)
                .content("댓글1")
                .board(Board.builder().id(boardId).build())
                .writer(User.builder().id(10L).name("tester1").build())
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .content("댓글2")
                .board(Board.builder().id(boardId).build())
                .writer(User.builder().id(11L).name("tester1").build())
                .build();

        Page<Comment> commentPage = new PageImpl<>(List.of(comment1, comment2), pageable, 2);
        when(commentRepository.findAllByBoardId(boardId, pageable)).thenReturn(commentPage);

        // when
        Page<CommentResponseDto> responseDto = commentService.getComment(boardId, pageable);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getTotalElements()).isEqualTo(2);
        assertThat(responseDto.getContent()).hasSize(2);
        assertThat(responseDto.getContent().get(0).getContent()).isEqualTo("댓글1");

    }

    @Test
    void 정상_댓글수정() {
        // given
        Long boardId = 1L;
        Long commentId = 100L;
        Long userId = 10L;
        String newContent = "수정된 댓글 내용";

        CommentRequestDto requestDto = new CommentRequestDto(newContent);
        CustomUserPrincipal authUser = new CustomUserPrincipal(userId, "test@example.com", UserRole.GUEST);

        Board board = Board.builder()
                .id(boardId)
                .build();

        User user = User.builder()
                .id(userId)
                .build();

        Comment comment = Comment.builder()
                .id(commentId)
                .board(board)
                .writer(user)
                .content("기존 댓글 내용")
                .build();

        when(boardSearchService.getBoardById(boardId)).thenReturn(board);
        when(userService.findById(userId)).thenReturn(user);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        CommentResponseDto responseDto = commentService.updateComment(boardId, commentId, requestDto, authUser);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(commentId);
        assertThat(responseDto.getBoardId()).isEqualTo(boardId);
        assertThat(responseDto.getUserId()).isEqualTo(userId);

        verify(commentRepository).flush();

    }

    @Test
    void 정상_댓글삭제() {
        // given
        Long boardId = 1L;
        Long commentId = 100L;
        Long userId = 10L;

        CustomUserPrincipal authUser = new CustomUserPrincipal(userId, "test@example.com", UserRole.GUEST);

        Board board = Board.builder()
                .id(boardId)
                .build();

        User user = User.builder()
                .id(userId)
                .build();

        Comment comment = Comment.builder()
                .id(commentId)
                .board(board)
                .writer(user)
                .content("삭제할 댓글")
                .build();

        when(boardSearchService.getBoardById(boardId)).thenReturn(board);
        when(userService.findById(userId)).thenReturn(user);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        commentService.deleteComment(boardId, commentId, authUser);

        // then
        verify(commentRepository, times(1)).delete(comment);

    }
}