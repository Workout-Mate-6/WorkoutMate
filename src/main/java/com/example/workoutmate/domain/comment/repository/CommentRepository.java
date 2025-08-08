package com.example.workoutmate.domain.comment.repository;

import com.example.workoutmate.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByBoardId(Long boardId, Pageable pageable);

    Optional<Comment> findById(Long commentId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.writer WHERE c.board.id = :boardId")
    List<Comment> findByBoardIdWithWriter(@Param("boardId") Long boardId);
}
