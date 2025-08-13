package com.example.workoutmate.global.util;

import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.domain.user.service.RefreshTokenService;
import com.example.workoutmate.domain.user.service.TokenBlacklistService;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j(topic = "jwtUtil")
@Component
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    // test 용 1분
    private static final long TOKEN_TIME = 1 * 60 * 1000L; // 1 분
    private static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;

    @Value("${SECRET_KEY}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public JwtUtil(TokenBlacklistService tokenBlacklistService, RefreshTokenService refreshTokenService) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(Long userId, String email, UserRole userRole) {
        Date date = new Date();
        String jti = UUID.randomUUID().toString();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .setId(jti)
                        .claim("email", email)
                        .claim("userRole", userRole)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new CustomException(CustomErrorCode.SERVER_EXCEPTION_JWT);
    }

    // 토큰 추출
    public Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    // Jti(access token 고유 식별자) 추출
    public String getJti(String token) {
        return parseToken(token).getBody().getId();
    }

    // Expiration(만료일) 추출
    public Date getExpiration(String token) {
        return parseToken(token).getBody().getExpiration();
    }

    // refresh token
    public String createRefreshToken(Long userId) {
        Date date = new Date();
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setId(jti)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateAccessToken(String token) {
        try {
            Jws<Claims> claims = parseToken(token);
            // 블랙리스트에 있는지 확인
            String jti = claims.getBody().getId();
            if (tokenBlacklistService.isBlacklisted(jti)) {
                return false;
            }
            return true;
        } // 토큰 만료
        catch (ExpiredJwtException e) {
            log.warn("JWT expired at {}", e.getClaims().getExpiration());
            return false;
        } // 서명 불일치, 구조 손상, 잘못된 인자 등
        catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    // 리프레시 토큰 유효성 검증
    public boolean validateRefreshToken(String refreshToken){
        try{
            // 토큰 파싱
            Claims claims = parseToken(refreshToken).getBody();
            String jti = claims.getId();
            Long userIdFromRedis = refreshTokenService.getUserIdByJti(jti);
            if(userIdFromRedis == null ){
                return false;
            }

            Long userIdFromToken = Long.valueOf(claims.getSubject());
            return userIdFromToken.equals(userIdFromRedis);
        }
        catch (ExpiredJwtException e) {
            log.warn("JWT expired at {}", e.getClaims().getExpiration());
            return false;
        } // 서명 불일치, 구조 손상, 잘못된 인자 등
        catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

}
