package com.example.workoutmate.global.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomErrorCode {
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    ENUM_TYPE_MISMATCH(HttpStatus.NOT_FOUND,"ENUM에 존재하지 않은 값입니다."),

    // DB 조건 위반
    DATA_INTEGRITY_VIOLATION(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 무결성 위반입니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "DB에 이미 존재하는 리소스입니다."),
    FK_CONSTRAINT_VIOLATION(HttpStatus.INTERNAL_SERVER_ERROR, "존재하지 않는 값을 FK로 참조하고 있습니다."),

    // 인증 인가
    SERVER_EXCEPTION_JWT(HttpStatus.INTERNAL_SERVER_ERROR, "Not Found Token"),
    SC_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
    SC_BAD_REQUEST(HttpStatus.BAD_REQUEST,"지원되지 않는 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "만료된 JWT 토큰입니다."),
    SC_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패 했습니다"),
    EMAIL_NOT_VERIFIED_FOR_SIGNUP(HttpStatus.CONFLICT,  "이미 가입 대기 중인 이메일입니다. 인증코드를 다시 받으려면 [재발송] 버튼을 눌러주세요."),
    EMAIL_INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "인증코드가 올바르지 않습니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증코드가 만료되었습니다. 재발송 요청 후 다시 시도해 주세요."),
    EMAIL_NOT_VERIFIED_FOR_LOGIN(HttpStatus.UNAUTHORIZED,  "이메일 인증이 완료되지 않았습니다"),
    ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증된 사용자 입니다."),

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
    INVALID_MAX_PARTICIPANTS(HttpStatus.BAD_REQUEST, "모집인원은 모집 확정 인원보다 커야합니다."),
    BOARD_HAS_PARTICIPANTS(HttpStatus.BAD_REQUEST, "현재 참여자가 있어 게시글을 삭제할 수 없습니다."),
    CACHE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인기글 리스트 캐싱에 실패했습니다."), // ← 추가

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    COMMENT_NOT_IN_BOARD(HttpStatus.BAD_REQUEST, "댓글이 해당 게시물에 속하지 않습니다."),
    UNAUTHORIZED_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "본인의 댓글만 수정 또는 삭제할 수 있습니다."),

    // SendGrid-Api
    SENDGRID_API_KEY_MISSING(HttpStatus.INTERNAL_SERVER_ERROR, "SendGrid API 키가 설정되지 않았습니다."),
    SENDGRID_API_KEY_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "SendGrid API 키 형식이 올바르지 않습니다."),

    // Chatting
    EQUALS_SENDER_RECEIVER(HttpStatus.BAD_REQUEST, "본인과는 채팅방을 생성할 수 없습니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    CHATROOM_DELETED(HttpStatus.BAD_REQUEST, "삭제된 채팅방입니다."),
    CHAT_ROOM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 채팅방이 존재합니다."),
    CHATROOM_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방 멤버가 존재하지 않습니다."),
    ALREADY_LEFT_CHATROOM(HttpStatus.BAD_REQUEST, "이미 채팅방을 나간 유저입니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "헤더에 JWT 토큰이 존재하지 않습니다."),
    TOKEN_USER_MISMATCH(HttpStatus.BAD_REQUEST, "유저 정보가 일치하지 않습니다."),

    // Zzim
    ALREADY_ZZIM(HttpStatus.CONFLICT, "이미 찜한 게시글입니다."),
    CANNOT_ZZIM_OWN_BOARD(HttpStatus.BAD_REQUEST, "본인이 작성한 게시글은 찜할 수 없습니다."),
    ZZIM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 찜 정보를 찾을 수 없습니다."),
    FORBIDDEN_ZZIM_ACCESS(HttpStatus.FORBIDDEN, "해당 찜에 대한 권한이 없습니다."),

    // participation
    DUPLICATE_APPLICATION(HttpStatus.CONFLICT, "이미 신청한 결과가 있습니다."),
    PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 요청을 찾을수 없습니다."),
    USER_RECEIVED_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "회원님께 온 요청이 없습니다."),
    SELF_PARTICIPATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "게시글 작성자는 신청할 수 없습니다."),
    ALREADY_STATE(HttpStatus.BAD_REQUEST,"이미 처리된 요청입니다."), // 이거 하나로 한번만 누르면 되는걸로 하자
    INVALID_STATE_TRANSITION(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED_PARTICIPATION(HttpStatus.BAD_REQUEST,"해당 요청은 사용자의 요청이 아닙니다."),
    BOARD_FULL(HttpStatus.CONFLICT,"모집인원이 가득찼습니다.")

    ;

    private final HttpStatus httpStatus;
    private final String message;

    CustomErrorCode(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
