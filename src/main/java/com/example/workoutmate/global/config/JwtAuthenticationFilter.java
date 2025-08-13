package com.example.workoutmate.global.config;

import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.domain.user.service.TokenBlacklistService;
import com.example.workoutmate.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklist;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = request;
        HttpServletResponse httpResponse = response;

        String bearerJwt = httpRequest.getHeader("Authorization");

        if (bearerJwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 리프레시 토큰 검사에서 제외
        if ("/api/auth/refresh".equals(request.getRequestURI())){
            filterChain.doFilter(request,response);
            return;
        }

        String token = jwtUtil.substringToken(bearerJwt);

        try {
            Claims claims = jwtUtil.extractClaims(token);
            if (claims == null) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            // 사용자 정보 추출
            Long userId = Long.valueOf(jws.getBody().getSubject());
            String email = jws.getBody().get("email", String.class);
            UserRole role = UserRole.valueOf((String) jws.getBody().get("userRole"));

            CustomUserPrincipal customUserPrincipal = new CustomUserPrincipal(userId, email, role);

            // SecurityContext 에 인증 등록
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    customUserPrincipal,null, customUserPrincipal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (MalformedJwtException | SecurityException e) {
            log.error("Invalid JWT signature, 유효하지 않은 JWT 서명입니다.", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 서명입니다.");
            return;}
        catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않은 JWT 서명입니다.", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 서명입니다.");
            return;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT token 입니다.");
            return;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰 입니다.");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
