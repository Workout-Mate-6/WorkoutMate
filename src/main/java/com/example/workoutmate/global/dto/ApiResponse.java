package com.example.workoutmate.global.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    // 응답 생성자
    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 성공 - 데이터 있음 + 커스텀 메시지
    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus code, String message, T data) {
        return ResponseEntity.status(code).body(new ApiResponse<>(true, message, data));
    }

    // 실패
    public static <T> ResponseEntity<ApiResponse<T>> failure(HttpStatus code, String message) {
        return ResponseEntity.status(code).body(new ApiResponse<>(false, message, null));
    }
}
