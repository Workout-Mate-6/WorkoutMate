package com.example.workoutmate.domain.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;

    public void saveRefreshTokenJti(String jti, Long userId){
        redisTemplate.opsForValue().set("refresh:"+jti, userId.toString(), 7, TimeUnit.DAYS);
    }

    public Long getUserIdByJti(String jti){
        return Long.valueOf(redisTemplate.opsForValue().get("refresh:"+ jti));
    }

    public void deleteRefreshTokenJti(String jti){
        redisTemplate.delete("refresh:"+jti);
    }
}
