package com.example.workoutmate.domain.recommend.controller;

import com.example.workoutmate.domain.recommend.v3.service.VectorMigrationService;
import com.example.workoutmate.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 벡터 마이그레이션 관리 컨트롤러
 * - 관리자만 실행 가능
 */
@RestController
@RequestMapping("/admin/vectors")
@RequiredArgsConstructor
public class VectorMigrationController {

    private final VectorMigrationService migrationService;

    /**
     * 전체 벡터 마이그레이션 실행
     * - 기존 유저/게시글 중 벡터가 없는 데이터에 벡터 생성
     */
    @PostMapping("/migrate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VectorMigrationService.MigrationResult>> migrateVectors() {
        VectorMigrationService.MigrationResult result = migrationService.migrateAll();
        return ApiResponse.success(HttpStatus.OK, result.getSummary(), result);
    }

    /**
     * 유저 벡터만 마이그레이션
     */
    @PostMapping("/migrate/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> migrateUserVectors() {
        int count = migrationService.migrateUserVectors();
        return ApiResponse.success(HttpStatus.OK,
                String.format("%d user vectors created", count), count);
    }

    /**
     * 게시글 벡터만 마이그레이션
     */
    @PostMapping("/migrate/boards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> migrateBoardVectors() {
        int count = migrationService.migrateBoardVectors();
        return ApiResponse.success(HttpStatus.OK,
                String.format("%d board vectors created", count), count);
    }
}
