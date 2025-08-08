package com.example.workoutmate.domain.chatting.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatAuthHandler chatAuthHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 경로 설정 - 목적지에서 메시지를 받을 수 있는 주소 설정
        registry.enableSimpleBroker(
                "/sub/chat/mates"
                , "/queue" // 에러 처리를 위한 주소
        );

        // 발행 경로 설정 - 해당 주소에서 메시지를 보냄
        registry.setApplicationDestinationPrefixes("/pub");

        // convertAndSendToUser() 전송을 위한 설정
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry
                .addEndpoint("/ws/chat")
                // CORS 설정 - 허용할 origin 패턴 설정
                .setAllowedOriginPatterns("*")
                // WebSocket을 지원하지 않는 브라우저를 위한 SockJS 지원 추가
                .withSockJS()
        ;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatAuthHandler);
    }
}
