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
    SC_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),

    // User
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "유저를 찾을 수 없습니다."),
    GENDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "유효하지 않은 성별입니다."),

    // follow
    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 팔로워 할 수 없습니다."),
    CANNOT_UNFOLLOW_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 언팔로워 할 수 없습니다."),
    ALLREADY_FOLLOWING(HttpStatus.CONFLICT, "이미 팔로워 중입니다."),
    NOT_FOLLOWING(HttpStatus.BAD_REQUEST, "팔로우 중이지 않은 사용자입니다."),

    // Board
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    UNAUTHORIZED_BOARD_ACCESS(HttpStatus.FORBIDDEN, "본인의 게시글만 수정 또는 삭제가 가능합니다."),


    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    COMMENT_NOT_IN_BOARD(HttpStatus.BAD_REQUEST, "댓글이 해당 게시물에 속하지 않습니다."),
    UNAUTHORIZED_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "본인의 댓글만 수정 또는 삭제할 수 있습니다."),

    // participation
    DUPLICATE_APPLICATION(HttpStatus.CONFLICT, "이미 신청한 결과가 있습니다."),
    PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 요청을 찾을수 없습니다."),
    USER_RECEIVED_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "회원님께 온 요청이 없습니다."),
    SELF_PARTICIPATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "게시글 작성자는 신청할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    CustomErrorCode(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
