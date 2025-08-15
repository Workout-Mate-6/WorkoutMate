package com.example.workoutmate.domain.board.controller;

import com.example.workoutmate.domain.board.dto.BoardFilterRequestDto;
import com.example.workoutmate.domain.board.dto.BoardRequestDto;
import com.example.workoutmate.domain.board.dto.BoardResponseDto;
import com.example.workoutmate.domain.board.dto.PopularBoardDto;
import com.example.workoutmate.domain.board.dto.BoardSportTypeResponseDto;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.board.service.BoardPopularityService;
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
    private final BoardPopularityService boardPopularityService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<BoardResponseDto>> createBoard(
            @Valid @RequestBody BoardRequestDto boardRequestDto,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
            ) {
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
    @GetMapping("/following")
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getBoardsFromFollowings(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = customUserPrincipal.getId();

        Page<BoardResponseDto> boardResponseDtoPage = boardService.getBoardsFromFollowings(userId, pageable);

        return ApiResponse.success(HttpStatus.OK, "팔로잉 유저 게시글 조회가 완료되었습니다.", boardResponseDtoPage);
    }


    // 운동 종목 별 카테고리 조회
    @GetMapping("/category")
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getBoardsByCategory(
            @RequestParam SportType sportType,
            @PageableDefault(size = 10, sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        Page<BoardResponseDto> boardResponseDtoPage = boardService.getBoardsByCategory(sportType, pageable);

        return ApiResponse.success(HttpStatus.OK, "운동 카테고리 별 게시글 조회 성공", boardResponseDtoPage);
    }

    // 내 게시물 조회하기
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getMyBoards(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<BoardResponseDto> boardResponseDtoPage = boardService.getMyBoards(customUserPrincipal, pageable);

        return ApiResponse.success(HttpStatus.OK, "내 게시글 목록이 조회에 완료되었습니다.", boardResponseDtoPage);
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequestDto boardRequestDto,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ) {
        Long userId = customUserPrincipal.getId();

        BoardResponseDto updatedBoard = boardService.updateBoard(boardId, userId, boardRequestDto);

        return ApiResponse.success(HttpStatus.OK, "게시글이 성공적으로 수정되었습니다." , updatedBoard);
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ) {
        Long userId = customUserPrincipal.getId();

        boardService.deleteBoard(boardId, userId);

        return ApiResponse.success(HttpStatus.OK, "게시글이 성공적으로 삭제되었습니다.", null);
    }

    // 운동 종목 카테고리 항목 조회
    @GetMapping("/sportType")
    public ResponseEntity<ApiResponse<BoardSportTypeResponseDto>> getAllSportTypes() {

        BoardSportTypeResponseDto responseDto = boardService.getAllSportTypes();

        return ApiResponse.success(HttpStatus.OK, "운동 종목 카테고리 전체 조회가 완료되었습니다.", responseDto);
    }

    // 전체 조회 기능 통합
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> filterBoards(
            @ModelAttribute BoardFilterRequestDto filterRequestDto,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @PageableDefault(size = 10, sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        Long userId = customUserPrincipal.getId();
        Page<BoardResponseDto> result = boardService.searchBoards(userId, filterRequestDto, pageable);

        return ApiResponse.success(HttpStatus.OK, "게시글 필터 조회 성공", result);
    }


    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularBoardDto>>> getPopularBoards() {
        List<PopularBoardDto> popularBoards = boardPopularityService.
                getPopularBoardsFromCache();
        return ApiResponse.success(HttpStatus.OK, "인기 게시글이 성공적으로 조회되었습니다.", popularBoards);
    }
}
