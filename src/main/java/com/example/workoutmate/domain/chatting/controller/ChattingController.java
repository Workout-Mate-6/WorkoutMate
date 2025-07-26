package com.example.workoutmate.domain.chatting.controller;

import com.example.workoutmate.domain.chatting.dto.ChatroomCreateResponseDto;
import com.example.workoutmate.domain.chatting.dto.ChatroomResponseDto;
import com.example.workoutmate.domain.chatting.service.ChattingService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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


    @GetMapping("/chatrooms/me")
    public ResponseEntity<ApiResponse<List<ChatroomResponseDto>>> getMyChatrooms(
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(defaultValue = "10") Integer size) {

        List<ChatroomResponseDto> chatroomList = chattingService.getMyChatrooms(authUser, cursor, size);

        return ApiResponse.success(HttpStatus.OK, "나의 채팅방 목록이 조회되었습니다.", chatroomList);
    }
}
