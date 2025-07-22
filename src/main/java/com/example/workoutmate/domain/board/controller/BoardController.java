package com.example.workoutmate.domain.board.controller;

import com.example.workoutmate.domain.board.controller.dto.BoardRequestDto;
import com.example.workoutmate.domain.board.controller.dto.BoardResponseDto;
import com.example.workoutmate.domain.board.service.BoardService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<BoardResponseDto>> createBoard(
            @Valid @RequestBody BoardRequestDto boardRequestDto,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal // jwt 토큰
            ) {

        // JWT에서 추출된 사용자 ID 사용
        Long userId = customUserPrincipal.getId();

        BoardResponseDto boardResponseDto = boardService.createBoard(userId, boardRequestDto);
        return ApiResponse.success(HttpStatus.CREATED, "게시글이 성공적으로 작성되었습니다.",boardResponseDto);
    }
}
