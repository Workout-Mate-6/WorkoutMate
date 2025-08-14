package com.example.workoutmate.domain.notification.dto;

import com.example.workoutmate.domain.notification.entity.Notification;
import com.example.workoutmate.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponseDto {
    private Long id;
    private String content;
    private NotificationType type;
    private String createdAt; // 날짜를 문자열로 변환해서 전송
    private boolean isRead;

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt().toString())
                .isRead(notification.getIsRead())
                .build();
    }
}
