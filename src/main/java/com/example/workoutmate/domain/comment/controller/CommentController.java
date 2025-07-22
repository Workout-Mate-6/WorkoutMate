package com.example.workoutmate.domain.comment.controller;

import com.example.workoutmate.domain.comment.dto.CommentRequestDto;
import com.example.workoutmate.domain.comment.dto.CommentResponseDto;
import com.example.workoutmate.domain.comment.service.CommentService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @RequestBody @Valid CommentRequestDto requestDto,
            @AuthenticationPrincipal CustomUserPrincipal CustomUser
            ){

        CommentResponseDto responseDto = commentService.createComment(boardId, requestDto, CustomUser);

        return ApiResponse.success(HttpStatus.CREATED, "댓글 생성 완료되었습니다.", responseDto);
    }


}
