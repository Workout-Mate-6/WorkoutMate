package com.example.workoutmate.domain.recommend.v3.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.recommend.v3.config.RecommendationProperties;
import com.example.workoutmate.domain.recommend.v3.feature.FeatureBuckets;
import com.example.workoutmate.domain.recommend.v3.store.BoardVectorEntity;
import com.example.workoutmate.domain.recommend.v3.store.BoardVectorRepository;
import com.example.workoutmate.domain.recommend.v3.vector.HashingEncoder;
import com.example.workoutmate.domain.recommend.v3.vector.VectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 게시글 컨텐츠를 임베딩 벡터로 변환/조회하는 서비스.
 * - 먼저 저장소(BoardVectorEntity)에서 벡터를 찾고, 없으면 encode()로 생성 후 저장.
 * - 벌크 버전(getOrEncodeBulk)은 후보 게시글 리스트에서 N회 반복 IO를 줄여준다.
 */
@Service
@RequiredArgsConstructor
public class BoardVectorService {
    private final RecommendationProperties props; // 벡터 차원 등 추천 관련 설정값
    private final BoardVectorRepository repo; // 벡터 저장/조회용 JPA 리포지토리


    /**
     * 주어진 Board 객체를 벡터로 인코딩
     */
    public float[] encode(Board b) {
        int dim = props.getVector().getDim(); // 백터 차원 설정값
        HashingEncoder enc = new HashingEncoder(dim); // 해시 기반 인코더 생성
        float[] v = VectorUtils.zeros(dim); // 벡터 초기화

        // 기본 피처 (필요에 따라 확장)
        enc.addFeature(v, FeatureBuckets.type(String.valueOf(b.getSportType())), 1.0); // 스포츠 종류
        enc.addFeature(v, FeatureBuckets.timeBucket(b.getStartTime()), 0.6); // 시작 시간대
        enc.addFeature(v, FeatureBuckets.dow(b.getStartTime()), 0.4); // 요일
        enc.addFeature(v, FeatureBuckets.sizeBucket(b.getMaxParticipants()), 0.3); // 최대 인원수
        // region 등 추가 가능

        float n = VectorUtils.l2Norm(v);
        if (n == 0.0f || Float.isNaN(n) || Float.isInfinite(n)) {
            java.util.Arrays.fill(v, 1f);               // 간단 균등 값
        }

        VectorUtils.l2Normalize(v);
        return v;
    }


    /**
     * 여러 Board 객체를 한 번에 처리 (배치)
     * - 이미 저장된 벡터는 조회만
     * - 없는 것은 새로 생성 후 저장
     */
    @Transactional
    public Map<Long, float[]> getOrEncodeBulk(List<Board> boards) {
        if (boards.isEmpty()) return Map.of();

        List<Long> ids = boards.stream().map(Board::getId).toList();

        // 기존 벡터 조회
        var existing = repo.findAllById(ids);               // IN 한 방
        Map<Long, float[]> out = new HashMap<>();
        for (var e : existing) out.put(e.getBoardId(), VectorUtils.fromBytes(e.getVec()));

        // 없으면 항목 새로 생성
        List<BoardVectorEntity> toSave = new ArrayList<>();
        for (Board b : boards) {
            if (out.containsKey(b.getId())) continue; // 이미 있으며 스킵
            float[] v = encode(b); // 인코딩
            out.put(b.getId(), v); // 결과를 맵에 추가
            toSave.add(BoardVectorEntity.builder() // 저장 대기 목록에 엔티티 빌드
                    .boardId(b.getId())
                    .vec(VectorUtils.toBytes(v))
                    .updatedAt(Instant.now())
                    .build());
        }

        // 새로 생성된 벡터 배치 저장
        if (!toSave.isEmpty()) repo.saveAll(toSave);        // 배치 저장
        return out;
    }
}
