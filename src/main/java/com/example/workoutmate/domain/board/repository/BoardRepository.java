package com.example.workoutmate.domain.board.repository;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 게시글 단건 조회
    Optional<Board> findByIdAndIsDeletedFalse(Long id);

    // 게시글 전체 조회
    Page<Board> findAllByIsDeletedFalse(Pageable pageable);

    // 내가 팔로잉한 사람들의 게시글 찾기
    Page<Board> findByWriter_IdIn(List<Long> writerIds, Pageable pageable);
    @Query(value = """
        select b from Board b
        join fetch b.writer w
        where w.id in :writerIds
        """,
            countQuery = """
        select count(b) from Board b
        where b.writer.id in :writerIds
        """)
    Page<Board> findAllByWriterIdInWithWriterFetch(@Param("writerIds") List<Long> writerIds, Pageable pageable);


    // 운동 종목 카테고리 검색 조회
    Page<Board> findAllByIsDeletedFalseAndSportType(Pageable pageable, SportType sportType);
}
