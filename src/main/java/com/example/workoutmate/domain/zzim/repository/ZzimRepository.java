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

//    long countByBoard(Board board);
}
