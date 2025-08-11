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

/**
 * 채팅 기능의 HTTP 요청을 처리하는 컨트롤러
 *
 * @author 이현하
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ChattingController {

    public final ChattingService chattingService;


    /**
     * 채팅방 생성
     *
     * @param userId 채팅할 상대 id
     * @param authUser 로그인한 유저 정보
     * @return 채팅방 정보 (새 채팅방 or 기존 채팅방)
     */
    @PostMapping("/chat-rooms/{userId}")
    public ResponseEntity<ApiResponse<ChatRoomCreateResponseDto>> createChatRoom(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {

        ChatRoomCreateResponseDto chatRoomCreateResponseDto = chattingService.createChatRoom(userId, authUser);

        return ApiResponse.success(HttpStatus.OK, "채팅방 생성 또는 조회가 완료되었습니다.", chatRoomCreateResponseDto);
    }


    /**
     * 내 채팅방 목록 조회
     *
     * @param authUser 로그인한 유저 정보
     * @param cursor 이전 페이지의 마지막 채팅방 메시지 시간 (ex.cursor=2025-07-25T18:45:00)
     * @param size 한 페이지에 조회할 채팅방 개수
     * @return 조회된 내 채팅방 정보
     */
    @GetMapping("/chat-rooms/me")
    public ResponseEntity<ApiResponse<List<ChatRoomResponseDto>>> getMyChatRooms(
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(defaultValue = "20") Integer size) {

        List<ChatRoomResponseDto> chatRoomList = chattingService.getMyChatRooms(authUser, cursor, size);

        return ApiResponse.success(HttpStatus.OK, "나의 채팅방 목록이 조회되었습니다.", chatRoomList);
    }


    /**
     * 채팅방 메시지 조회
     *
     * @param chatRoomId 조회할 채팅방 id
     * @param authUser 로그인한 유저 정보
     * @param cursor 이전 페이지의 마지막 메시지 id
     * @param size 한 페이지에 조회할 메시지 개수
     * @return 조회된 채팅방 메시지 정보
     */
    @GetMapping("/chat-rooms/message/{chatRoomId}")
    public ResponseEntity<ApiResponse<List<ChatMessageResponseDto>>> getChatRoomMessage(
        @PathVariable Long chatRoomId,
        @AuthenticationPrincipal CustomUserPrincipal authUser,
        @RequestParam(required = false) Long cursor,
        @RequestParam(defaultValue = "20") Integer size) {

        List<ChatMessageResponseDto> chatMessageList = chattingService.getChatRoomMessage(chatRoomId, authUser, cursor, size);

        return ApiResponse.success(HttpStatus.OK, "채팅방 메시지가 조회되었습니다.", chatMessageList);
    }


    /**
     * 채팅방 나가기
     *
     * @param chatRoomId 채팅방 id
     * @param authUser 로그인한 유저 정보
     */
    @DeleteMapping("/chat-rooms/{chatRoomId}/deletion")
    public ResponseEntity<ApiResponse<Void>> leaveChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {

        chattingService.leaveChatRoom(chatRoomId, authUser);

        return ApiResponse.success(HttpStatus.OK, "채팅방에서 퇴장하였습니다.", null);
    }
}
