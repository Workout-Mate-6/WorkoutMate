package com.example.workoutmate.global.scheduler;

import com.example.workoutmate.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class UnverifiedUserDeleteScheduler {

    private final AuthService authService;
    private final StringRedisTemplate stringRedisTemplate;

    @Scheduled(fixedRate = 5 * 60 * 1000) // 5분 마다 실행
    public void unverifiedUserDelete() {
        try {
            log.info("미인증 사용자 정리 시작");
            int deleted;
            int totalDeleted = 0;
            do {
                deleted = authService.findUnverifiedUsersAndDelete(LocalDateTime.now().minusMinutes(30), 1000); // 1000건씩
                totalDeleted += deleted;
                log.debug("배치 삭제 완료: {}명 (누적: {}명)", deleted, totalDeleted);
            } while (deleted == 1000);
            log.info("미인증 사용자 총 {}명 삭제 완료", totalDeleted);
        } catch (Exception e) {
            log.error("미인증 사용자 삭제 실패", e);
        }
        // Redis test (TTL 이전 키들이 사라지는 문제 확인을 위함)
        String userId = stringRedisTemplate.opsForValue().get("refresh:6ae359b6-29ce-4d3b-870a-9cd3eb692213");
        log.warn("Redis test 용 - key(refresh:6ae359b6-29ce-4d3b-870a-9cd3eb692213, 만료예정일 8/25) 존재함 userId={}", userId);
    }
}

