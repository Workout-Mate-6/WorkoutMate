package com.example.workoutmate.domain.notification.controller;

import com.example.workoutmate.domain.notification.service.NotificationService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}) // 개발용 로컬 HTML
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @RequestHeader(value = "Last-Event_ID", defaultValue = "", required = false) String lastEventId
            ) {

        if (customUserPrincipal == null) {
            // 인증 안된 상태면 401 혹은 다른 처리 가능
            return ResponseEntity.status(401).build();
        }

        Long userId = customUserPrincipal.getId();

        return ResponseEntity.ok(notificationService.subscribe(userId, lastEventId));
    }

}
