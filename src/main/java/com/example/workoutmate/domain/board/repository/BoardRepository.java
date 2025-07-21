package com.example.workoutmate.domain.board.repository;

import com.example.workoutmate.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
