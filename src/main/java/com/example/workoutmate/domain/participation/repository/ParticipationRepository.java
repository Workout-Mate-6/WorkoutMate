package com.example.workoutmate.domain.participation.repository;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation,Long> {

    boolean existsByBoardIdAndApplicantId(Long boardId, Long userId);

    Optional<Participation> findByBoardIdAndApplicantId(Long boardId, Long applicantId);

    //Optional<Participation> findByCommentIdAndApplicantId(Long commentId, Long applicantId);

    Optional<Participation> findByBoardAndApplicant(Board board, User applicant);
}
