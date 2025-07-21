package com.example.workoutmate.global.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomErrorCode {
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 인증 인가
    SERVER_EXCEPTION_JWT(HttpStatus.INTERNAL_SERVER_ERROR, "Not Found Token"),
    SC_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
    SC_BAD_REQUEST(HttpStatus.BAD_REQUEST,"지원되지 않는 JWT 토큰입니다."),
    SC_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    CustomErrorCode(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
