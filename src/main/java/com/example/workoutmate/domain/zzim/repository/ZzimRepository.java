package com.example.workoutmate.domain.zzim.repository;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.entity.Zzim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZzimRepository extends JpaRepository<Zzim, Long> {

    Optional<Zzim> findByBoardAndUser(Board board, User user);

    Page<Zzim> findAllByBoard(Board board, Pageable pageable);

    long countByBoard(Board board);

    // 본인이 찜한 게시글 전체 목록 조회
    Page<Zzim> findAllByUserId(Long userId, Pageable pageable);

    // 유저가 해당 게시글을 찜했는지 여부 조회
    Optional<Zzim> findByBoardIdAndUserId(Long boardId, Long userId);

    // 해당 유저의 모든 찜 리스트
    List<Zzim> findAllByUser(User user);

    List<Zzim> findAllByUserId(Long userId);
}
