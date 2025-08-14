package com.example.workoutmate.domain.notification.service;

import com.example.workoutmate.domain.notification.dto.NotificationResponseDto;
import com.example.workoutmate.domain.notification.entity.Notification;
import com.example.workoutmate.domain.notification.enums.NotificationType;
import com.example.workoutmate.domain.notification.repository.NotificationRepository;
import com.example.workoutmate.domain.notification.sse.EmitterRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * SSE 구독: emitter 생성 -> 더미(connected) 전송 -> DB에 남아있는 unread 전송(그리고 읽음 처리)
     */
    public SseEmitter subscribe(Long userId, String lastEventId) {

        // 기존에 같은 userId로 시작하는 emitter 삭제 (중복 방지)
        emitterRepository.deleteByUserIdPrefix(String.valueOf(userId));

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

        // 연결 성공 알림 전송
        sendToClient(emitter, emitterId, Map.of("message", "connected"));

        // DB에 남아있는 읽지 않은 알림 전송 및 읽음 처리
        List<Notification> unread = notificationRepository.findByReceiverIdAndIsReadFalse(userId);

        if (!unread.isEmpty()) {
            System.out.println("[SSE-구독] 전달할 DB 미수신 알림 수: " + unread.size());

            AtomicInteger counter = new AtomicInteger(0);
            unread.forEach(n -> {
                NotificationResponseDto notificationResponseDto = NotificationResponseDto.from(n);

                // 캐시 키를 userId_알림ID 형태로 변경 (중복 캐시 방지)
                String cacheKey = userId + "_" + n.getId();
                emitterRepository.saveEventCache(cacheKey, notificationResponseDto);

                sendToClient(emitter, cacheKey, notificationResponseDto);

                // 전송 성공 시 읽음 처리
                n.markAsRead();
            });
            notificationRepository.saveAll(unread);
        }

        // 이전 이벤트 전달 (단, DB 미수신 알림은 이미 전송했으므로 중복 방지 필터링)
        Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));

        List<Long> sentNotificationIds = unread.stream()
                .map(Notification::getId)
                .collect(Collectors.toList());

        events.entrySet().stream()
                .filter(e -> lastEventId.isEmpty() || lastEventId.compareTo(e.getKey()) < 0)
                // 캐시 이벤트 중 이미 보낸 DB 미수신 알림 제외
                .filter(e -> {
                    String key = e.getKey();
                    try {
                        String[] parts = key.split("_");
                        Long notificationId = Long.parseLong(parts[1]);
                        return !sentNotificationIds.contains(notificationId);
                    } catch (Exception ex) {
                        return true;
                    }
                })
                .forEach(e -> sendToClient(emitter, e.getKey(), e.getValue()));

        return emitter;
    }

    /**
     * 알림 생성 -> DB 저장 -> (실시간으로 전달 가능한 구독자가 있으면) SSE 전송하고 읽음 처리
     * 구독자가 없으면 DB에 isRead=false로 남아 있다가 다음 접속 시 subscribe()에서 전달됩니다.
     */
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

        // DTO 변환 (전송용)
        NotificationResponseDto notificationResponseDto = NotificationResponseDto.from(notification);

        String userIdKey = String.valueOf(receiver.getId());

        // 현재 연결된 emitter들을 찾음
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(userIdKey);

        System.out.println("[알림-대상Emitter] userId=" + userIdKey + ", emitterCount=" + emitters.size());

        if (emitters.isEmpty()) {
            // 연결자가 없으면 DB에 unread로 남겨둠
            return;
        }

        emitters.forEach((key, emitter) -> {
            // 캐시 키도 userId_알림ID 형태로 통일
            String cacheKey = userIdKey + "_" + notification.getId();
            emitterRepository.saveEventCache(cacheKey, notificationResponseDto);
            sendToClient(emitter, cacheKey, notificationResponseDto);
        });

        // 실시간으로 전송했기 때문에 DB상의 알림을 읽음 처리
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    /**
     * 클라이언트로 JSON을 보내는 공통 메서드
     */
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

    /**
     * (선택) REST로 읽지 않은 알림을 가져오고, 가져간 순간 읽음 처리
     */
    @Transactional
    public List<NotificationResponseDto> fetchUnreadAndMarkRead(Long userId) {
        List<Notification> unread = notificationRepository.findByReceiverIdAndIsReadFalse(userId);
        List<NotificationResponseDto> dtoList = unread.stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
        unread.forEach(Notification::markAsRead);
        notificationRepository.saveAll(unread);
        return dtoList;
    }

    /**
     * 개별 알림 읽음 처리
     */
    @Transactional
    public void markNotificationAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림이 존재하지 않습니다."));
        if (!notification.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        if (!notification.isRead()) {
            notification.markAsRead();
            notificationRepository.save(notification);
        }
    }

}
