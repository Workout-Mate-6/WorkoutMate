package com.example.workoutmate.domain.chatting.event;

import com.example.workoutmate.domain.chatting.dto.ChatDto;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * WebSocket 메시지 발행(Publishing)을 전담하는 클래스
 */
@Component
@RequiredArgsConstructor
public class ChatPublisher {

    private final SimpMessageSendingOperations template;

    public void sendMessage(Long chatRoomId, ChatDto message) {
        template.convertAndSend("/sub/chat/mates/" + chatRoomId, message);
    }

    public void sendError(Map<String, Object> errorPayload, CustomUserPrincipal principal) {
        template.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                errorPayload
        );
    }
}
