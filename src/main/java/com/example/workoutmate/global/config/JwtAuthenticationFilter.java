package com.example.workoutmate.global.config;

import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = request;
        HttpServletResponse httpResponse = response;

        String bearerJwt = httpRequest.getHeader("Authorization");

        /* html에서 토큰을 통해 서버에 연결하기 위해 토큰 인증 절차를 수정하였습니다.*/
        String token = null;

        if (bearerJwt != null && bearerJwt.startsWith("Bearer")) {
            token = jwtUtil.substringToken(bearerJwt);
        } else {
            // Authorization 헤더가 없으면 쿼리 파라미터에서 token을 가져오기
            token = request.getParameter("token");
        }

        if (token == null || token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        /* 여기 부분 까지 수정을 진행하였습니다.*/

        try {
            Claims claims = jwtUtil.extractClaims(token);
            if (claims == null) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            // 사용자 정보 추출
            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.get("email").toString();
            UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

            CustomUserPrincipal customUserPrincipal = new CustomUserPrincipal(userId, email, userRole);

            // SecurityContext 에 인증 등록
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    customUserPrincipal,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole.name()))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
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
        } catch (Exception e) {
            log.error("Internal server error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "예상치 못한 서버 에러입니다.");
            return;
        }
    }
}
