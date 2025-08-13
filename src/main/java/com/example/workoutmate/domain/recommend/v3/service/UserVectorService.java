package com.example.workoutmate.domain.recommend.v3.service;

import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.recommend.v3.config.RecommendationProperties;
import com.example.workoutmate.domain.recommend.v3.feature.FeatureBuckets;
import com.example.workoutmate.domain.recommend.v3.store.UserVectorEntity;
import com.example.workoutmate.domain.recommend.v3.store.UserVectorRepository;
import com.example.workoutmate.domain.recommend.v3.vector.HashingEncoder;
import com.example.workoutmate.domain.recommend.v3.vector.VectorUtils;
import com.example.workoutmate.domain.zzim.entity.Zzim;
import com.example.workoutmate.domain.zzim.repository.ZzimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * user 벡터 서비스
 */
@Service
@RequiredArgsConstructor
public class UserVectorService {
    private final RecommendationProperties props;
    private final UserVectorRepository repo;
    private final ParticipationService participationService; // 변경: Service 사용
    private final ZzimRepository zzimRepo;

    /**
     * 유저 행동 → 벡터. 최신성 가중, 조인/찜 반영. 필요 시 click/view도 추가 가능.
     */
    @Transactional(readOnly = true)
    public float[] buildFromBehavior(Long userId) {
        int dim = props.getVector().getDim();
        HashingEncoder enc = new HashingEncoder(dim);
        float[] acc = VectorUtils.zeros(dim);

        double decayPerDay = props.getVector().getDecayPerDay();
        Instant now = Instant.now();

        // 1) 참여 이력(주요 신호) — ParticipationService 사용
        var ps = participationService.findByApplicant_Id(userId);
        for (Participation p : ps) {
            if (p.getBoard() == null) continue;
            var b = p.getBoard();
            LocalDateTime t = b.getStartTime();
            Duration d = Duration.between(t != null ? t.atZone(ZoneId.systemDefault()).toInstant() : now, now);
            long days = Math.max(0, d.toDays());
            double w = Math.pow(decayPerDay, days) * 1.0; // 참여는 1.0 가중

            enc.addFeature(acc, FeatureBuckets.type(String.valueOf(b.getSportType())), w);
            enc.addFeature(acc, FeatureBuckets.timeBucket(b.getStartTime()), w * 0.6);
            enc.addFeature(acc, FeatureBuckets.dow(b.getStartTime()), w * 0.4);
            enc.addFeature(acc, FeatureBuckets.sizeBucket(b.getMaxParticipants()), w * 0.3);
        }

        // 2) 찜 이력(보조 신호)
        var zzims = zzimRepo.findAllByUserId(userId);
        if (zzims != null) {
            for (Zzim z : zzims) {
                double w = 0.5; // 고정 가중(최신성 반영하려면 createdAt 사용)
                enc.addFeature(acc, "zzim", w);
            }
        }

        // 전사 평균 혼합(콜드스타트 완화)
        double mix = props.getVector().getUserGlobalMix();
        if (mix > 0) {
            float[] global = globalPrior(dim);
            VectorUtils.addInPlace(acc, global, mix);
        }

        VectorUtils.l2Normalize(acc);
        return acc;
    }

    private float[] globalPrior(int dim) {
        float[] v = new float[dim];
        for (int i = 0; i < dim; i++) v[i] = 1f;
        VectorUtils.l2Normalize(v);
        return v;
    }

    @Transactional
    public void upsert(Long userId, float[] vec) {
        repo.save(UserVectorEntity.builder()
                .userId(userId)
                .vec(VectorUtils.toBytes(vec))
                .updatedAt(Instant.now())
                .build());
    }

    @Transactional(readOnly = true)
    public float[] getOrBuild(Long userId) {
        return repo.findById(userId)
                .map(x -> VectorUtils.fromBytes(x.getVec()))
                .orElseGet(() -> {
                    float[] v = buildFromBehavior(userId);
                    upsert(userId, v);
                    return v;
                });
    }
}
