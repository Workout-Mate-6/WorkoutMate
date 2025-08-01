package com.example.workoutmate.domain.chatting.controller;

import com.example.workoutmate.domain.chatting.dto.ChatDto;
import com.example.workoutmate.domain.chatting.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping
@Controller
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chats/send-message")
    public void sendMessage(@Payload ChatDto chat) {
        ChatDto chatDto = chatMessageService.save(chat);
        template.convertAndSend("/sub/chat/mates/" + chat.getChatRoomId(), chatDto);
    }
}
