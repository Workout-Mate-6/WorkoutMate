package com.example.workoutmate.domain.recommend.v3.scheduler;

import com.example.workoutmate.domain.recommend.v3.service.VectorMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 벡터 유지보수 스케줄러
 * - 주기적으로 벡터 없는 데이터 확인 및 생성
 */
@Component
@RequiredArgsConstructor
public class VectorMaintenanceScheduler {

    private final VectorMigrationService migrationService;

    /**
     * 매일 새벽 3시에 벡터 없는 데이터 확인 및 생성
     * 필요한 경우에만 주석 해제하여 사용
     */
    // @Scheduled(cron = "0 0 3 * * *")
    public void maintainVectors() {
        migrationService.migrateAll();
    }
}
