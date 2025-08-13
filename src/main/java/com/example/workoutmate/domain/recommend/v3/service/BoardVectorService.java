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
 * board 벡터 서비스
 */
@Service
@RequiredArgsConstructor
public class BoardVectorService {
    private final RecommendationProperties props;
    private final BoardVectorRepository repo;

    public float[] encode(Board b) {
        int dim = props.getVector().getDim();
        HashingEncoder enc = new HashingEncoder(dim);
        float[] v = VectorUtils.zeros(dim);

        // 기본 피처 (필요에 따라 확장)
        enc.addFeature(v, FeatureBuckets.type(String.valueOf(b.getSportType())), 1.0);
        enc.addFeature(v, FeatureBuckets.timeBucket(b.getStartTime()), 0.6);
        enc.addFeature(v, FeatureBuckets.dow(b.getStartTime()), 0.4);
        enc.addFeature(v, FeatureBuckets.sizeBucket(b.getMaxParticipants()), 0.3);
        // region 등 추가 가능

        VectorUtils.l2Normalize(v);
        return v;
    }

    public void upsert(Board b) {
        float[] v = encode(b);
        BoardVectorEntity e = BoardVectorEntity.builder()
                .boardId(b.getId())
                .vec(VectorUtils.toBytes(v))
                .updatedAt(Instant.now())
                .build();
        repo.save(e);
    }

    public float[] getOrEncode(Board b) {
        return repo.findById(b.getId())
                .map(x -> VectorUtils.fromBytes(x.getVec()))
                .orElseGet(() -> {
                    float[] v = encode(b);
                    repo.save(BoardVectorEntity.builder()
                            .boardId(b.getId())
                            .vec(VectorUtils.toBytes(v))
                            .updatedAt(Instant.now())
                            .build());
                    return v;
                });
    }

    @Transactional
    public Map<Long, float[]> getOrEncodeBulk(List<Board> boards) {
        if (boards.isEmpty()) return Map.of();
        List<Long> ids = boards.stream().map(Board::getId).toList();
        var existing = repo.findAllById(ids);               // IN 한 방
        Map<Long, float[]> out = new HashMap<>();
        for (var e : existing) out.put(e.getBoardId(), VectorUtils.fromBytes(e.getVec()));
        List<BoardVectorEntity> toSave = new ArrayList<>();
        for (Board b : boards) {
            if (out.containsKey(b.getId())) continue;
            float[] v = encode(b);
            out.put(b.getId(), v);
            toSave.add(BoardVectorEntity.builder()
                    .boardId(b.getId())
                    .vec(VectorUtils.toBytes(v))
                    .updatedAt(Instant.now())
                    .build());
        }
        if (!toSave.isEmpty()) repo.saveAll(toSave);        // 배치 저장
        return out;
    }
}
