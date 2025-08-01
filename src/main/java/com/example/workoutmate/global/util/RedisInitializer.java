package com.example.workoutmate.global.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisInitializer {

    // 인텔리제이 실행 할 때 캐시를 초기화 시키는 클래스 입니다. 이거 주석 처리하고 사용하셔도 됩니다!
    private final RedisTemplate<String, String> stringRedisTemplate;

    @PostConstruct
    public void initializeRedis() {
        // ZSet 초기화
        stringRedisTemplate.delete("board:view:ranking");
        // 캐시도 초기화
        stringRedisTemplate.delete("board:popular:top10");

        log.info("Redis 인기글 데이터 초기화 완료");
    }
}
