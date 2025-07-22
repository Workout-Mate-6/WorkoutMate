package com.example.workoutmate.domain.user.controller;

import com.example.workoutmate.domain.user.dto.UserEditRequestDto;
import com.example.workoutmate.domain.user.dto.UserEditResponseDto;
import com.example.workoutmate.domain.user.dto.WithdrawRequestDto;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 유저 기능의 HTTP 요청을 처리하는 컨트롤러
 *
 * @author 이현하
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * 유저 정보 수정
     *
     * @param requestDto 수정할 유저 정보
     * @param authUser   로그인한 유저 정보
     * @return 수정된 유저 정보
     */
    @PatchMapping("/users/me")
    public ResponseEntity<ApiResponse<UserEditResponseDto>> editUserInfo(
            @Valid @RequestBody UserEditRequestDto requestDto,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {

        UserEditResponseDto userEditResponseDto = userService.editUserInfo(authUser, requestDto);

        return ApiResponse.success(HttpStatus.OK, "회원 정보 수정이 완료되었습니다.", userEditResponseDto);
    }


    /**
     * 유저 탈퇴
     *
     * @param requestDto 비밀번호
     * @param authUser   로그인한 유저 정보
     * @return HTTP 상태 코드
     */
    @PostMapping("/users/me/deletion")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @RequestBody WithdrawRequestDto requestDto,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {

        userService.deleteUser(authUser, requestDto);

        return ApiResponse.success(HttpStatus.OK, "회원 탈퇴가 완료되었습니다.", null);
    }
}
