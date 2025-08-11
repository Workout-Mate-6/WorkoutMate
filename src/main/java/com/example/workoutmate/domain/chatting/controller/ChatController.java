package com.example.workoutmate.domain.chatting.controller;

import com.example.workoutmate.domain.chatting.dto.ChatDto;
import com.example.workoutmate.domain.chatting.service.ChatMessageService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping
@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;

    // 채팅 전송 요청
    @MessageMapping("/chats/send-message")
    public void sendMessage(@Payload ChatDto chat,
                            CustomUserPrincipal user) {

        chatMessageService.saveAndSend(chat, user.getEmail());
    }

    // 토큰 갱신 요청
    @MessageMapping("/chat.ping")
    public void handlePing(@Header("Authorization") String token,
                           CustomUserPrincipal user) {

        chatMessageService.sendPingError(token, user);
    }
}
