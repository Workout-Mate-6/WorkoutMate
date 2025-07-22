package com.example.workoutmate.domain.board.controller;

import com.example.workoutmate.domain.board.controller.dto.BoardRequestDto;
import com.example.workoutmate.domain.board.controller.dto.BoardResponseDto;
import com.example.workoutmate.domain.board.service.BoardService;
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

    // 게시글 단건 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> getBoard(@PathVariable Long boardId) {

        BoardResponseDto BoardResponseDto = boardService.getBoard(boardId);

        return ApiResponse.success(HttpStatus.OK, "게시글 조회에 성공했습니다.", BoardResponseDto);
    }

    // 게시글 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getAllBoards(
            @PageableDefault(size = 10, sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {

        Page<BoardResponseDto> boardResponseDtoPage = boardService.getAllBoards(pageable);

        return ApiResponse.success(HttpStatus.OK, "게시글 전체 조회가 완료되었습니다.", boardResponseDtoPage);
    }

    // 팔로잉 유저 게시글 전체 조회



}
