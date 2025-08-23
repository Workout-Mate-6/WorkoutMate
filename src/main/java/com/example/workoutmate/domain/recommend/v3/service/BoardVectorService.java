package com.example.workoutmate.domain.recommend.v3.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.recommend.v3.config.RecommendationProperties;
import com.example.workoutmate.domain.recommend.v3.feature.FeatureBuckets;
import com.example.workoutmate.domain.recommend.v3.store.BoardVectorEntity;
import com.example.workoutmate.domain.recommend.v3.store.BoardVectorRepository;
import com.example.workoutmate.domain.recommend.v3.vector.HashingEncoder;
import com.example.workoutmate.domain.recommend.v3.vector.VectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
        int dim = props.getVector().getDim();
        HashingEncoder enc = new HashingEncoder(dim);
        float[] v = VectorUtils.zeros(dim);

        // null 안전성 체크
        String sportType = b.getSportType() != null ? String.valueOf(b.getSportType()) : "UNKNOWN";
        enc.addFeature(v, FeatureBuckets.type(sportType), 1.0);

        if (b.getStartTime() != null) {
            enc.addFeature(v, FeatureBuckets.timeBucket(b.getStartTime()), 0.6);
            enc.addFeature(v, FeatureBuckets.dow(b.getStartTime()), 0.4);
        }

        if (b.getMaxParticipants() != null) {
            enc.addFeature(v, FeatureBuckets.sizeBucket(b.getMaxParticipants()), 0.3);
        }

        float n = VectorUtils.l2Norm(v);
        if (n == 0.0f || Float.isNaN(n) || Float.isInfinite(n)) {
            // 의미있는 기본값으로 대체 (모든 차원에 작은 값)
            for (int i = 0; i < dim; i++) {
                v[i] = 1.0f / (float)Math.sqrt(dim); // 단위벡터가 되도록
            }
        } else {
            VectorUtils.l2Normalize(v);
        }

        return v;
    }

    /**
     * 벡터를 계산하여 DB에 저장 또는 갱신
     */
    @Transactional
    public void upsert(Board b) {
        float[] v = encode(b);

        var existing = repo.findById(b.getId());
        if (existing.isPresent()) {
            // 업데이트
            BoardVectorEntity entity = existing.get();
            entity.setVec(VectorUtils.toBytes(v));
            entity.setUpdatedAt(Instant.now());
            repo.save(entity);
        } else {
            // 새로 생성
            asyncSaveVector(b.getId(), v);
        }
    }

    public float[] getOrEncode(Board b) {
        return repo.findById(b.getId())
                .map(x -> VectorUtils.fromBytes(x.getVec()))
                .orElseGet(() -> {
                    float[] v = encode(b);
                    asyncSaveVector(b.getId(), v);
                    return v;
                });
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
        var existing = repo.findAllById(ids);
        Map<Long, float[]> out = new HashMap<>();
        for (var e : existing) {
            out.put(e.getBoardId(), VectorUtils.fromBytes(e.getVec()));
        }

        // 없는 항목들 개별 처리 (트랜잭션 안정성)
        for (Board b : boards) {
            if (out.containsKey(b.getId())) continue;

            float[] v = encode(b);
            out.put(b.getId(), v);

            // 개별 저장 시도 (실패해도 계속 진행)
            asyncSaveVector(b.getId(), v);
        }

        return out;
    }

    private void asyncSaveVector(Long boardId, float[] vector) {
        try {
            BoardVectorEntity entity = BoardVectorEntity.builder()
                    .boardId(boardId)
                    .vec(VectorUtils.toBytes(vector))
                    .updatedAt(Instant.now())
                    .build();
            repo.save(entity);
        } catch (DataIntegrityViolationException e) {
            // 다른 스레드가 이미 생성했으면 무시
        }
    }

    @Transactional(readOnly = true)
    public boolean hasVector(Long boardId) {
        return repo.existsById(boardId);
    }

    @Transactional
    public void createVectorForNewBoard(Board board) {
        float[] vector = encode(board);
        asyncSaveVector(board.getId(), vector);
    }

    /**
     * 새 게시글의 벡터 생성
     * - 게시글 작성 완료 시점에 호출
     * - 게시글 특성(종목, 시간, 규모)을 벡터로 변환
     */
    @Transactional
    public void createBoardVector(Board board) {
        // 이미 벡터가 있는지 확인
        if (repo.existsById(board.getId())) {
            return;
        }

        // 게시글 특성 기반 벡터 생성
        float[] vector = encode(board);

        // DB에 저장
        BoardVectorEntity entity = BoardVectorEntity.builder()
                .boardId(board.getId())
                .vec(VectorUtils.toBytes(vector))
                .updatedAt(Instant.now())
                .build();

        repo.save(entity);
    }

    /**
     * 게시글 수정 시 벡터 업데이트
     * - 운동 종목, 시간, 인원수 등이 변경될 때 호출
     */
    @Transactional
    public void updateBoardVector(Board board) {
        // 새로운 벡터 계산
        float[] updatedVector = encode(board);

        var existing = repo.findById(board.getId());
        if (existing.isPresent()) {
            // 기존 벡터 업데이트
            BoardVectorEntity entity = existing.get();
            entity.setVec(VectorUtils.toBytes(updatedVector));
            entity.setUpdatedAt(Instant.now());
            repo.save(entity);
        } else {
            // 없으면 새로 생성
            createBoardVector(board);
        }
    }

    /**
     * 벡터 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean hasBoardVector(Long boardId) {
        return repo.existsById(boardId);
    }
}
