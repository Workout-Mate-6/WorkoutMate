package com.example.workoutmate.domain.zzim.controller;

import com.example.workoutmate.domain.zzim.controller.dto.ZzimResponseDto;
import com.example.workoutmate.domain.zzim.service.ZzimService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
