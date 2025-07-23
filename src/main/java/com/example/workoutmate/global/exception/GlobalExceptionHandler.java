package com.example.workoutmate.global.exception;

import com.example.workoutmate.global.dto.CustomErrorResponseDto;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.util.CustomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.example.workoutmate.global.enums.CustomErrorCode.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponseDto> handleCustomException(CustomException e) {
        CustomErrorCode errorCode = e.getErrorCode();

        CustomErrorResponseDto errorResponseDto = new CustomErrorResponseDto(errorCode.name(),e.getMessage());

        return new ResponseEntity<>(errorResponseDto, errorCode.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        CustomErrorCode errorCode = CustomErrorCode.INVALID_REQUEST;
        String message = messageSource.getMessage(Objects.requireNonNull(e.getFieldError()), Locale.KOREA);
        CustomErrorResponseDto errorResponseDto = new CustomErrorResponseDto(errorCode.name(), message);

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(CustomMapper.responseToMap(errorResponseDto, false));
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {

        String dbMessage = e.getMessage();

        CustomErrorCode errorCode = DATA_INTEGRITY_VIOLATION;

        if (dbMessage != null && dbMessage.contains("Duplicate entry")) {
            errorCode = DUPLICATE_RESOURCE;
        } else if (dbMessage != null && dbMessage.contains("foreign key constraint fails")) {
            errorCode = FK_CONSTRAINT_VIOLATION;
        }

        CustomErrorResponseDto errorResponseDto = new CustomErrorResponseDto(errorCode.name(), errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(CustomMapper.responseToMap(errorResponseDto, false));
    }
}
