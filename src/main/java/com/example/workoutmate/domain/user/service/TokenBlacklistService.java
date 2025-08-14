package com.example.workoutmate.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    // blacklist 에 토큰 추가
    public void addToBlacklist(String jti, Duration ttl){
        redisTemplate.opsForValue().set("blacklist:"+jti, "true", ttl);
    }

    // blacklist 에 있는 토큰인지 검증
    public boolean isBlacklisted(String jti){
        return redisTemplate.hasKey("blacklist:"+jti);
    }
}