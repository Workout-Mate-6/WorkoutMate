package com.example.workoutmate.domain.chatting.config;

import com.example.workoutmate.domain.chatting.event.ChatPublisher;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class StompExceptionHandler {

    private final SimpMessagingTemplate template;
    private final ChatPublisher chatPublisher;

    @MessageExceptionHandler(CustomException.class)
    public void handleCustomException(CustomException e, CustomUserPrincipal principal) {
        log.error("STOMP Error - USER: {}, Message: {}", principal.getName(), e.getMessage());

        // 에러 정보를 담을 페이로드 생성
        Map<String, Object> errorPayload = new HashMap<>();
        errorPayload.put("error", e.getClass().getSimpleName());
        errorPayload.put("message", e.getMessage());

        // 에러 메시지 전송
        chatPublisher.sendError(errorPayload, principal);
    }
}
