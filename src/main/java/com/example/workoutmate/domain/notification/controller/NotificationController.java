package com.example.workoutmate.domain.notification.controller;

import com.example.workoutmate.domain.notification.dto.NotificationResponseDto;
import com.example.workoutmate.domain.notification.service.NotificationService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://workoutmate.kro.kr"},
        allowCredentials = "true") // 개발용 로컬 HTML (실제 주소가 생기면 변경해야함)
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @RequestAttribute("customUserPrincipal") CustomUserPrincipal customUserPrincipal,
            @RequestHeader(value = "Last-Event_ID", defaultValue = "", required = false) String lastEventId
            ) {

        if (customUserPrincipal == null) {
            // 인증 안된 상태면 401 혹은 다른 처리 가능
            return ResponseEntity.status(401).build();
        }

        Long userId = customUserPrincipal.getId();

        return ResponseEntity.ok(notificationService.subscribe(userId, lastEventId));
    }

    // 클라이언트가 원할 때 읽지 않은 알림을 한 번에 가져오고 읽음 처리
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnread(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ) {
        if (customUserPrincipal == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = customUserPrincipal.getId();
        return ResponseEntity.ok(notificationService.fetchUnreadAndMarkRead(userId));
    }

    // 알림 읽음 처리 API 추가
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @PathVariable Long notificationId) {

        if (customUserPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        notificationService.markNotificationAsRead(customUserPrincipal.getId(), notificationId);
        return ResponseEntity.ok().build();
    }
}
