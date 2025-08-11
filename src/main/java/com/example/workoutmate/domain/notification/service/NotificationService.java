package com.example.workoutmate.domain.notification.service;

import com.example.workoutmate.domain.notification.dto.NotificationResponseDto;
import com.example.workoutmate.domain.notification.entity.Notification;
import com.example.workoutmate.domain.notification.enums.NotificationType;
import com.example.workoutmate.domain.notification.repository.NotificationRepository;
import com.example.workoutmate.domain.notification.sse.EmitterRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SseEmitter subscribe(Long userId, String lastEventId) {
        String emitterId = userId + "_" + System.currentTimeMillis();
        System.out.println("[SSE-구독] userId=" + userId + ", emitterId=" + emitterId);

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> {
            emitterRepository.deleteById(emitterId);
            System.out.println("[SSE-종료] emitterId= " + emitterId);
        });


        emitter.onTimeout(() -> {
            emitterRepository.deleteById(emitterId);
            System.out.println("[SSE-타임아웃] emitterId= " + emitterId);
        });

        // 연결이 되었음을 알리는 더미 데이터 전송
        sendToClient(emitter, emitterId, "connected");

        // 이전 이벤트 전달
        Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));

        // lastEventId가 없으면 전부, 있으면 이후 이벤트만 전송
        events.entrySet().stream()
                .filter(e -> lastEventId.isEmpty() || lastEventId.compareTo(e.getKey()) < 0)
                .forEach(e -> sendToClient(emitter, e.getKey(), e.getValue()));

        return emitter;
    }

    @Transactional
    public void sendNotification(User receiver, NotificationType type, String content) {
        System.out.println("[알림-전송요청] receiverId=" + receiver.getId() + ", type=" + type + ", content=" + content);


        Notification notification = notificationRepository.save(
                Notification.builder()
                        .receiver(receiver)
                        .type(type)
                        .content(content)
                        .isRead(false)
                        .build()
        );

        // DTO 변환
        NotificationResponseDto notificationResponseDto = NotificationResponseDto.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt().toString()) // 문자열로 변환
                .isRead(notification.getIsRead())
                .build();

        String userIdKey = String.valueOf(receiver.getId());

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(userIdKey);

        System.out.println("[알림-대상Emitter] userId=" + userIdKey + ", emitterCount=" + emitters.size());

        emitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, notificationResponseDto);
            sendToClient(emitter, key, notificationResponseDto);
        });
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {

        try {
            String jsonData;

            if (data instanceof String) {
                // 문자열이면 JSON 문자열로 감싸기
                jsonData = objectMapper.writeValueAsString(Map.of("message", data));
            } else {
                // 객체면 JSON으로 직렬화
                jsonData = objectMapper.writeValueAsString(data);
            }

            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(jsonData)
                    .name("notification") // 이벤트 이름
            );
            System.out.println("[알림-전송완료] emitterId=" + emitterId + ", data=" + jsonData);

        } catch (IOException e) {
            System.out.println("[알림-전송실패] emitterId=" + emitterId + ", error=" + e.getMessage());
            emitterRepository.deleteById(emitterId);
        }
    }
}
