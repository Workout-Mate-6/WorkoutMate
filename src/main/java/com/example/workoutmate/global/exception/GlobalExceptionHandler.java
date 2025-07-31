package com.example.workoutmate.global.exception;

import com.example.workoutmate.global.dto.ApiResponse;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
        return ApiResponse.failure(errorCode.getHttpStatus(), message);
    }

    // Valid 관련 예외 관리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = messageSource.getMessage(Objects.requireNonNull(e.getFieldError()), Locale.KOREA);
        return ApiResponse.failure(CustomErrorCode.INVALID_REQUEST.getHttpStatus(), message);
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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleEnumTypeMissMatch(MethodArgumentTypeMismatchException e) {
        if (e.getRequiredType() != null && e.getRequiredType().isEnum()) {
            String value = String.valueOf(e.getValue());
            String typename = e.getRequiredType().getSimpleName();
            String message = String.format("[%s]는 %s 에 존재하지 않는 값입니다.", value, typename);
            return ApiResponse.failure(CustomErrorCode.ENUM_TYPE_MISMATCH.getHttpStatus(), message);
        }
        return ApiResponse.failure(CustomErrorCode.INVALID_REQUEST.getHttpStatus(), "요청 파라미터 타입이 올바르지 않습니다.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingRequestparmeter(MissingServletRequestParameterException e) {
        String message = String.format("필수 요청 파라미터가 누락되었습니다: [%s]", e.getParameterName());
        return ApiResponse.failure(INVALID_REQUEST.getHttpStatus(), message);
    }

    // 요청 body에 있는 필드가 Dto에 없을 시
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<?> handleUnrecognizedPropertyException(UnrecognizedPropertyException e) {
        String message = String.format("요청에 포함된 잘못된 필드명입니다: [%s] 필드는 사용할 수 없습니다.", e.getPropertyName());
        return ApiResponse.failure(INVALID_REQUEST.getHttpStatus(), message);
    }

    // HTTP 메시지 읽기(파싱) 예외처리
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
//        return ApiResponse.failure(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다");
//    }
}
