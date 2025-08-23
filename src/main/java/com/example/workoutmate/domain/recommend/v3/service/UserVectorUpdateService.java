package com.example.workoutmate.domain.recommend.v3.service;

import com.example.workoutmate.domain.recommend.v3.config.RecommendationProperties;
import com.example.workoutmate.domain.recommend.v3.store.UserVectorEntity;
import com.example.workoutmate.domain.recommend.v3.store.UserVectorRepository;
import com.example.workoutmate.domain.recommend.v3.vector.VectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserVectorUpdateService {

    private final UserVectorRepository userVectorRepository;
    private final RecommendationProperties props;

    /**
     * 신규 유저를 위한 초기 벡터 생성
     */
    @Transactional
    public void createInitialUserVector(Long userId) {
        // 이미 벡터가 있는지 확인
        if (userVectorRepository.existsById(userId)) {
            return;
        }

        // 기본 벡터 생성
        int dim = props.getVector().getDim();
        float[] initialVector = createDefaultVectorForNewUser(dim);

        // DB에 저장
        UserVectorEntity entity = UserVectorEntity.builder()
                .userId(userId)
                .vec(VectorUtils.toBytes(initialVector))
                .updatedAt(Instant.now())
                .build();

        userVectorRepository.save(entity);
    }

    /**
     * 유저 벡터 업데이트 트리거
     * - 참여, 찜 등의 활동 시 호출
     * - 실제 벡터 계산은 UserVectorService에서 처리
     */
    @Transactional
    public void triggerVectorUpdate(Long userId) {
        // 벡터가 없으면 기본 벡터 생성
        if (!userVectorRepository.existsById(userId)) {
            createInitialUserVector(userId);
        }

        // 업데이트 시간만 갱신 (실제 벡터 재계산은 나중에 배치로)
        userVectorRepository.findById(userId).ifPresent(entity -> {
            entity.setUpdatedAt(Instant.now());
            userVectorRepository.save(entity);
        });
    }

    /**
     * 신규 유저용 기본 벡터 생성
     */
    private float[] createDefaultVectorForNewUser(int dim) {
        float[] vector = new float[dim];

        // 균등한 초기값 설정
        float initialValue = 1.0f / (float) Math.sqrt(dim);
        for (int i = 0; i < dim; i++) {
            vector[i] = initialValue;
        }

        // L2 정규화
        float norm = VectorUtils.l2Norm(vector);
        if (norm > 0 && !Float.isNaN(norm) && !Float.isInfinite(norm)) {
            VectorUtils.l2Normalize(vector);
        }

        return vector;
    }
}
