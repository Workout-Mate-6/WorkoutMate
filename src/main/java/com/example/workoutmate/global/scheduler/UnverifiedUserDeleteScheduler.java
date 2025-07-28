package com.example.workoutmate.global.scheduler;

import com.example.workoutmate.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Scheduled(fixedRate = 5 * 60 * 1000) // 5분 마다 실행
    public void unverifiedUserDelete() {
        try {
            log.info("미인증 사용자 정리 시작");
            int deleted;
            int totalDeleted = 0;
            do {
                deleted = authService.findUnverifiedUsersAndDelete(LocalDateTime.now().minusMinutes(3), 1000); // 1000건씩
                totalDeleted += deleted;
                log.debug("배치 삭제 완료: {}명 (누적: {}명)", deleted, totalDeleted);
            } while (deleted == 1000);
            log.info("미인증 사용자 총 {}명 삭제 완료", totalDeleted);
        } catch (Exception e) {
            log.error("미인증 사용자 삭제 실패", e);
        }
    }
}
