package com.example.workoutmate.domain.board.repository;

import com.example.workoutmate.domain.board.dto.BoardFilterRequestDto;
import com.example.workoutmate.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardQueryRepository {
    Page<Board> searchWithFilters(Long userId, BoardFilterRequestDto boardFilterRequestDto, Pageable pageable);
}
