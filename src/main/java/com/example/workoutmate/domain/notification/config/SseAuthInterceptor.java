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
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = jwtUtil.substringToken(authHeader);
        try {
            Claims claims = jwtUtil.extractClaims(token);
            if (claims == null) throw new CustomException(TOKEN_NOT_FOUND, TOKEN_NOT_FOUND.getMessage());

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
