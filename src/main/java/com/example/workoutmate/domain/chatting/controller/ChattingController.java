package com.example.workoutmate.domain.chatting.controller;

import com.example.workoutmate.domain.chatting.dto.ChatroomCreateResponseDto;
import com.example.workoutmate.domain.chatting.service.ChattingService;
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
@RequestMapping
@RequiredArgsConstructor
public class ChattingController {

    public final ChattingService chattingService;

    @PostMapping("/chatrooms/{userId}")
    public ResponseEntity<ApiResponse<ChatroomCreateResponseDto>> createChatRoom(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {

        ChatroomCreateResponseDto chatroomCreateResponseDto = chattingService.createChatRoom(userId, authUser);

        return ApiResponse.success(HttpStatus.OK, "채팅방 생성 또는 조회가 완료되었습니다.", chatroomCreateResponseDto);
    }
}
