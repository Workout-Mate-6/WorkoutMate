package com.example.workoutmate.domain.chatting.controller;

import com.example.workoutmate.domain.chatting.dto.ChatMessageResponseDto;
import com.example.workoutmate.domain.chatting.dto.ChatRoomCreateResponseDto;
import com.example.workoutmate.domain.chatting.dto.ChatRoomResponseDto;
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

    @PostMapping("/chat_rooms/{userId}")
    public ResponseEntity<ApiResponse<ChatRoomCreateResponseDto>> createChatRoom(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {

        ChatRoomCreateResponseDto chatRoomCreateResponseDto = chattingService.createChatRoom(userId, authUser);

        return ApiResponse.success(HttpStatus.OK, "채팅방 생성 또는 조회가 완료되었습니다.", chatRoomCreateResponseDto);
    }


    @GetMapping("/chat_rooms/me")
    public ResponseEntity<ApiResponse<List<ChatRoomResponseDto>>> getMyChatRooms(
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(defaultValue = "20") Integer size) {

        List<ChatRoomResponseDto> chatRoomList = chattingService.getMyChatRooms(authUser, cursor, size);

        return ApiResponse.success(HttpStatus.OK, "나의 채팅방 목록이 조회되었습니다.", chatRoomList);
    }

    @GetMapping("/chat_rooms/message/{chatRoomId}")
    public ResponseEntity<ApiResponse<List<ChatMessageResponseDto>>> getChatRoomMessage(
        @PathVariable Long chatRoomId,
        @AuthenticationPrincipal CustomUserPrincipal authUser,
        @RequestParam(required = false) Long cursor,
        @RequestParam(defaultValue = "20") Integer size) {

        List<ChatMessageResponseDto> chatMessageList = chattingService.getChatRoomMessage(chatRoomId, authUser, cursor, size);

        return ApiResponse.success(HttpStatus.OK, "채팅방 메시지가 조회되었습니다.", chatMessageList);
    }


    @DeleteMapping("/chat_rooms/{chatRoomId}/deletion")
    public ResponseEntity<ApiResponse<Void>> leaveChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {

        chattingService.leaveChatRoom(chatRoomId, authUser);

        return ApiResponse.success(HttpStatus.OK, "채팅방에서 퇴장하였습니다.", null);
    }
}
