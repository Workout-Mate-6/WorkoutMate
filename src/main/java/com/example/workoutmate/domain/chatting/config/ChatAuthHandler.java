package com.example.workoutmate.domain.chatting.config;

import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.exception.CustomException;
import com.example.workoutmate.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;


import static com.example.workoutmate.global.enums.CustomErrorCode.TOKEN_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatAuthHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            String token = jwtUtil.substringToken(jwtToken);

            try {
                Claims claims = jwtUtil.parseToken(token).getBody();

                if (claims == null) {
                    throw new CustomException(TOKEN_NOT_FOUND, TOKEN_NOT_FOUND.getMessage());
                }

                Long userId = Long.valueOf(claims.getSubject());
                String email = claims.get("email", String.class);
                String role = claims.get("userRole", String.class);

                CustomUserPrincipal userPrincipal = new CustomUserPrincipal(userId, email, UserRole.valueOf(role));

                accessor.setUser(userPrincipal);
                accessor.getSessionAttributes().put("user", userPrincipal);

                return message;

            } catch (Exception e) {
                log.error("WebSocket Handler JWT 에러", e);
                throw new CustomException(TOKEN_NOT_FOUND, TOKEN_NOT_FOUND.getMessage());
            }
        }

        return message;
    }
}
