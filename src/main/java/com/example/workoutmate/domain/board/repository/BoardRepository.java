package com.example.workoutmate.domain.board.repository;

import com.example.workoutmate.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 게시글 단건 조회
    Optional<Board> findByIdAndIsDeletedFalse(Long id);

    // 게시글 전체 조회
    List<Board> findAllByIsDeletedFalse();
}
