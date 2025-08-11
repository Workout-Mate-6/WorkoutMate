package com.example.workoutmate.global.util;

import com.example.workoutmate.domain.user.enums.UserRole;
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

import static com.example.workoutmate.global.enums.CustomErrorCode.EXPIRED_JWT_TOKEN;
import static com.example.workoutmate.global.enums.CustomErrorCode.SC_UNAUTHORIZED;

@Slf4j(topic = "jwtUtil")
@Component
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60*60*1000L;

    @Value("${SECRET_KEY}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init(){
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(Long userId, String email, UserRole userRole){
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .claim("email", email)
                        .claim("userRole", userRole)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public String substringToken(String tokenValue){
        if(StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)){
            return tokenValue.substring(7);
        }
        throw new CustomException(CustomErrorCode.SERVER_EXCEPTION_JWT);
    }

    public Claims extractClaims(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomException(EXPIRED_JWT_TOKEN, EXPIRED_JWT_TOKEN.getMessage());
        } catch (JwtException e) {
            throw new CustomException(SC_UNAUTHORIZED, SC_UNAUTHORIZED.getMessage());
        }
    }
}
