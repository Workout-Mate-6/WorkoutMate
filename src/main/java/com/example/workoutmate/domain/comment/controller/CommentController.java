package com.example.workoutmate.domain.comment.controller;

import com.example.workoutmate.domain.comment.dto.CommentRequestDto;
import com.example.workoutmate.domain.comment.dto.CommentResponseDto;
import com.example.workoutmate.domain.comment.service.CommentService;
import com.example.workoutmate.domain.comment.dto.CommentWithParticipationStatusResponseDto;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @PathVariable Long boardId,
            @Valid @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal CustomUserPrincipal authUser
            ){

        CommentResponseDto responseDto = commentService.createComment(boardId, requestDto, authUser);

        return ApiResponse.success(HttpStatus.CREATED, "댓글 생성 완료되었습니다.", responseDto);
    }

    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponseDto>>> getComment(
            @PathVariable Long boardId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ){

        Page<CommentResponseDto> responseDto = commentService.getComment(boardId, pageable);

        return ApiResponse.success(HttpStatus.OK, "댓글 조회에 성공하였습니다.", responseDto);
    }

    @PutMapping("/boards/{boardId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ){
        CommentResponseDto responseDto = commentService.updateComment(boardId, commentId, requestDto, authUser);

        return ApiResponse.success(HttpStatus.OK, "댓글 수정이 완료되었습니다.", responseDto);
    }

    @DeleteMapping("/boards/{boardId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ){
        commentService.deleteComment(boardId, commentId, authUser);

        return ApiResponse.success(HttpStatus.OK, "댓글 삭제가 완료되었습니다.", null);
    }

    @GetMapping("/boards/{boardId}/comments/participations")
    public ResponseEntity<ApiResponse<List<CommentWithParticipationStatusResponseDto>>> getCommentWithParticipation(
            @PathVariable Long boardId
    ) {

        return ApiResponse.success(HttpStatus.OK,
                "조회 되었습니다.",
                commentService.getCommentWithParticipation(boardId));
    }

}
