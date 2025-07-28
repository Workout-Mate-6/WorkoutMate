package com.example.workoutmate.domain.zzim.controller;

import com.example.workoutmate.domain.zzim.controller.dto.ZzimCountResponseDto;
import com.example.workoutmate.domain.zzim.controller.dto.ZzimResponseDto;
import com.example.workoutmate.domain.zzim.controller.dto.ZzimStatusResponseDto;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class ZzimController {

    private final ZzimService zzimService;

    @PostMapping("/{boardId}/zzims")
    public ResponseEntity<ApiResponse<ZzimResponseDto>> createZzim(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
            ) {

        Long userId = customUserPrincipal.getId();
        ZzimResponseDto responseDto = zzimService.createZzim(boardId, userId);

        return ApiResponse.success(HttpStatus.CREATED, "찜이 성공적으로 생성되었습니다.", responseDto);
    }

    @GetMapping("/{boardId}/zzims")
    public ResponseEntity<ApiResponse<Page<ZzimResponseDto>>> getZzimsByBoard(
            @PathVariable Long boardId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        Page<ZzimResponseDto> zzimResponseDtoPages = zzimService.getZzimsByBoardId(boardId, pageable);

        return ApiResponse.success(HttpStatus.OK, "게시글 찜 전체 조회가 완료되었습니다.", zzimResponseDtoPages);
    }

    @GetMapping("/{boardId}/zzims/count")
    public ResponseEntity<ApiResponse<ZzimCountResponseDto>> getZzimCountByBoard(
            @PathVariable Long boardId
    ) {
        ZzimCountResponseDto responseDto = zzimService.getZzimCountByBoardId(boardId);

        return ApiResponse.success(HttpStatus.OK, "게시글 찜 전체 갯수 조회가 완료되었습니다.", responseDto);
    }

    @GetMapping("/zzims/users")
    public ResponseEntity<ApiResponse<Page<ZzimResponseDto>>> getUserZzims(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = customUserPrincipal.getId();

        Page<ZzimResponseDto> zzimResponseDtoPage = zzimService.getUserZzims(userId, pageable);

        return ApiResponse.success(HttpStatus.OK, "유저의 찜 전체 목록 조회가 완료되었습니다.", zzimResponseDtoPage);
    }

    @GetMapping("/{boardId}/zzims/me")
    public ResponseEntity<ApiResponse<ZzimStatusResponseDto>> checkIfZzimmed(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ) {
        Long userId = customUserPrincipal.getId();

        ZzimStatusResponseDto responseDto = zzimService.checkZzimStatus(boardId, userId);

        return ApiResponse.success(HttpStatus.OK, "유저의 찜 여부 조회 성공", responseDto);
    }

}
