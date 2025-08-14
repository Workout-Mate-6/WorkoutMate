package com.example.workoutmate.domain.participation.repository;

import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation,Long> {

    boolean existsByBoardIdAndApplicantId(Long boardId, Long userId);

    Optional<Participation> findByBoardIdAndApplicantId(Long boardId, Long applicantId);

    //Optional<Participation> findByCommentIdAndApplicantId(Long commentId, Long applicantId);

    //Optional<Participation> findByBoardAndApplicant(Board board, User applicant);

    List<Participation> findByBoardIdAndApplicant_IdIn(Long boardId, List<Long> userId);

    @Query("SELECT p.board.id, p.applicant.id FROM Participation p WHERE p.board.id IN :boardIds AND p.state = :state")
    List<Object[]> findBoardIdAndUserIdByBoardIdsAndState(@Param("boardIds") List<Long> boardId,
                                                          @Param("state") ParticipationState state);

    List<Participation> findAllByApplicant_Id(Long applicantId);
}
