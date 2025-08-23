package com.example.workoutmate.domain.recommend.v3.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BoardVectorRepository extends JpaRepository<BoardVectorEntity, Long> {
    // 명시적 벌크 조회
    List<BoardVectorEntity> findByBoardIdIn(Collection<Long> boardIds);
}
