package com.example.workoutmate.domain.chatting.service;

import com.example.workoutmate.domain.chatting.dto.ChatDto;
import com.example.workoutmate.domain.chatting.entity.ChatMessage;
import com.example.workoutmate.domain.chatting.entity.ChatRoomMember;
import com.example.workoutmate.domain.chatting.entity.ChattingMapper;
import com.example.workoutmate.domain.chatting.event.ChatPublisher;
import com.example.workoutmate.domain.chatting.repository.ChatMessageRepository;
import com.example.workoutmate.domain.chatting.repository.ChatRoomMemberRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.exception.CustomException;
import com.example.workoutmate.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.workoutmate.global.enums.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserService userService;
    private final ChatPublisher chatPublisher;
    private final JwtUtil jwtUtil;

    @Transactional
    public void saveAndSend(ChatDto chatDto, String email) {
        User sender = userService.findByEmail(email);

        // 채팅방 퇴장 여부 확인
        // 1. 채팅방 멤버 정보 조회
        ChatRoomMember member = chatRoomMemberRepository.findByChatRoomIdAndUserId(chatDto.getChatRoomId(), sender.getId())
                .orElseThrow(() -> new CustomException(CHATROOM_MEMBER_NOT_FOUND, "채팅방의 멤버가 아닙니다."));

        // 2. 해당 채팅방에서 나갔는지 확인
        if (!member.isJoined()) {
            // STOMP 예외 처리 핸들러에서 처리할 수 있는 특정 예외를 발생시키는 것이 좋음
            throw new CustomException(ALREADY_LEFT_CHATROOM, "이미 퇴장한 채팅방에서는 메시지를 보낼 수 없습니다.");
        }

        // 메시지 전송
        chatDto.setTypeTalk();
        ChatMessage chatMessage = ChattingMapper.toChatMessage(chatDto, sender);

        chatMessageRepository.save(chatMessage);

        ChatDto chat = ChattingMapper.toChatDto(chatMessage);

        chatPublisher.sendMessage(chat.getChatRoomId(), chat);
    }


    public void sendPingError(String token, CustomUserPrincipal authUser) {
        Claims claims = jwtUtil.parseToken(jwtUtil.substringToken(token)).getBody();
        String email = claims.get("email", String.class);

        if (!email.equals(authUser.getEmail())) {
            throw new CustomException(TOKEN_USER_MISMATCH, TOKEN_USER_MISMATCH.getMessage() + " 다시 로그인해주세요.");
        }
    }
}
