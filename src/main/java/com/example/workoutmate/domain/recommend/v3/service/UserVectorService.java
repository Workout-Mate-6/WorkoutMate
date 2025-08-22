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
import org.springframework.dao.DataIntegrityViolationException;
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
     * 유저 행동 → 벡터 생성.
     * - 참여(Participation) 기록을 시간 감쇠(decay)로 가중
     * - 보드의 타입/시간/요일/모집규모 등을 feature bucket 으로 해시 인코딩
     * - 찜(zzim) 이력은 보조 가중
     * - 전사 평균 벡터를 혼합(userGlobalMix)해 콜드스타트 완화
     * - 마지막에 L2 정규화(코사인 유사도 계산에 유리)
     *
     * @return L2-정규화된 유저 벡터(float[])
     */
    public float[] buildFromBehavior(Long userId) {
        int dim = props.getVector().getDim(); // 벡터 차원 우리프로젝트에서는 dim:32 로 되어있음
        HashingEncoder enc = new HashingEncoder(dim); // 해시 기반 인코더(동일 feature → 동일 index 누적)
        float[] acc = VectorUtils.zeros(dim); // 누적 버퍼(0으로 초기화)

        double decayPerDay = props.getVector().getDecayPerDay(); // 1일 경과당 감쇠율
        Instant now = Instant.now(); // 현재시각

        // 참여 이력(주요 신호) — ParticipationService 사용
        var ps = participationService.findByApplicant_Id(userId);
        for (Participation p : ps) {
            if (p.getBoard() == null) continue;
            var b = p.getBoard();

            // 시작 시간 -> 현재 시각 까지의 일수 계산
            // 현재 구현은 "보드에서 모임시간(startTime) 기준"으로 감쇠
            LocalDateTime t = b.getStartTime();
            Duration d = Duration.between(t != null ? t.atZone(ZoneId.systemDefault()).toInstant() : now, now);
            long days = Math.max(0, d.toDays());
            double w = Math.pow(decayPerDay, days) * 1.0; // 참여는 1.0 가중


            // 종목 타입: 가장 강한 콘텐츠 특징
            enc.addFeature(acc, FeatureBuckets.type(String.valueOf(b.getSportType())), w);
            // 시간대 bucket: 시작시간을 coarse-grain 으로 버킷팅 → 시간 선호(아침/저녁 등) 반영
            enc.addFeature(acc, FeatureBuckets.timeBucket(b.getStartTime()), w * 0.6);
            // 요일 bucket: 요일 선호(주말/평일) 반영
            enc.addFeature(acc, FeatureBuckets.dow(b.getStartTime()), w * 0.4);
            // 모집 규모 bucket: 소수정예/대규모 선호 반영
            enc.addFeature(acc, FeatureBuckets.sizeBucket(b.getMaxParticipants()), w * 0.3);
        }

        // 찜 이력(보조 신호)
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

        float norm = VectorUtils.l2Norm(acc);
        if (norm == 0.0f || Float.isNaN(norm) || Float.isInfinite(norm)) {
            float[] prior = globalPrior(dim);           // 균등 단위벡터(내 코드에 이미 존재)
            VectorUtils.addInPlace(acc, prior, 1.0f);   // 최소량 주입
        }

        VectorUtils.l2Normalize(acc);
        return acc;
    }

    /**
     * 전사 평균 벡터 생성
     * - 모든 차원 값이 동일한 단위 벡터
     */
    private float[] globalPrior(int dim) {
        float[] v = new float[dim];
        for (int i = 0; i < dim; i++) v[i] = 1f;
        VectorUtils.l2Normalize(v);
        return v;
    }


    /**
     * 유저 벡터 저장 또는 갱신
     */
    @Transactional
    public void upsert(Long userId, float[] vec) {
        try {
            UserVectorEntity entity = UserVectorEntity.builder()
                    .userId(userId)
                    .vec(VectorUtils.toBytes(vec))
                    .updatedAt(Instant.now())
                    .build();
            repo.save(entity);
        } catch (DataIntegrityViolationException e) {
            // 이미 존재하면 업데이트
            var existing = repo.findById(userId);
            if (existing.isPresent()) {
                UserVectorEntity entity = existing.get();
                entity.setVec(VectorUtils.toBytes(vec));
                entity.setUpdatedAt(Instant.now());
                repo.save(entity);
            }
        }
    }

    /**
     * 저장된 유저 벡터를 가져오거나, 없으면 생성하여 저장 후 반환
     */
    @Transactional(readOnly = true)
    public float[] getOrBuild(Long userId) {
        var existing = repo.findById(userId);
        if (existing.isPresent()) {
            return VectorUtils.fromBytes(existing.get().getVec());
        }

        return createDefaultVector();
    }

    @Transactional
    public void createUserVectorIfNotExists(Long userId) {
        try {
            float[] vector = buildFromBehavior(userId);
            UserVectorEntity entity = UserVectorEntity.builder()
                    .userId(userId)
                    .vec(VectorUtils.toBytes(vector))
                    .updatedAt(Instant.now())
                    .build();
            repo.save(entity);
        } catch (DataIntegrityViolationException e) {
            // 중복키 예외 무시 (다른 스레드가 이미 생성함)
        }
    }

    private float[] createDefaultVector() {
        int dim = props.getVector().getDim();
        float[] v = globalPrior(dim);

        // 정규화 및 안전성 체크
        float norm = VectorUtils.l2Norm(v);
        if (norm == 0.0f || Float.isNaN(norm) || Float.isInfinite(norm)) {
            // 모든 차원 동일값으로 초기화
            for (int i = 0; i < dim; i++) v[i] = 1f;
        }
        VectorUtils.l2Normalize(v);
        return v;
    }
}
