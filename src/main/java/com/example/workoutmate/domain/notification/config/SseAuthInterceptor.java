package com.example.workoutmate.domain.notification.config;

import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.exception.CustomException;
import com.example.workoutmate.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.example.workoutmate.global.enums.CustomErrorCode.TOKEN_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = null;

        // 1) Authorization 헤더 확인
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = jwtUtil.substringToken(authHeader);
        }

        // 2) 헤더 없으면 ?token= 쿼리 파라미터 확인
        if (token == null) {
            token = request.getParameter("token");
        }

        if (token == null || token.isEmpty()) {
            log.warn("SSE 요청 토큰 없음");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            // 수정: extractClaims 대신 parseToken().getBody() 사용
            Claims claims = jwtUtil.parseToken(token).getBody();

            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.get("email", String.class);
            String role = claims.get("userRole", String.class);

            CustomUserPrincipal userPrincipal = new CustomUserPrincipal(userId, email, UserRole.valueOf(role));
            request.setAttribute("customUserPrincipal", userPrincipal);

            return true;
        } catch (Exception e) {
            log.error("SSE JWT 인증 실패", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
