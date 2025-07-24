package com.example.workoutmate.domain.zzim.repository;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.zzim.entity.Zzim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZzimRepository extends JpaRepository<Zzim, Long> {

    Optional<Zzim> findByBoardAndUser(Board board, User user);
}
