package com.example.workoutmate.domain.notification.dto;

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
}
