package com.example.workoutmate.global.exception;

import com.example.workoutmate.global.dto.ApiResponse;
import com.example.workoutmate.global.enums.CustomErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Locale;
import java.util.Objects;

import static com.example.workoutmate.global.enums.CustomErrorCode.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // 커스텀 예외 관리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {
        CustomErrorCode errorCode = e.getErrorCode();
        String message = e.getMessage();
        return ApiResponse.failure(errorCode.getHttpStatus(),message);
    }

    // Valid 관련 예외 관리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = messageSource.getMessage(Objects.requireNonNull(e.getFieldError()), Locale.KOREA);
        return ApiResponse.failure(CustomErrorCode.INVALID_REQUEST.getHttpStatus(),message);
    }

    // DB 무결성 관련 예외 관리
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        String dbMessage = e.getMessage();

        CustomErrorCode errorCode = DATA_INTEGRITY_VIOLATION;
        if (dbMessage != null && dbMessage.contains("Duplicate entry")) {
            errorCode = DUPLICATE_RESOURCE;
        } else if (dbMessage != null && dbMessage.contains("foreign key constraint fails")) {
            errorCode = FK_CONSTRAINT_VIOLATION;
        }

        return ApiResponse.failure(errorCode.getHttpStatus(), errorCode.getMessage());
    }

    // HTTP 메시지 읽기(파싱) 예외처리
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
//        return ApiResponse.failure(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다");
//    }
}
