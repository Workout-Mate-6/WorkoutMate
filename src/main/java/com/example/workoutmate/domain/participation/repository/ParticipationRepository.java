package com.example.workoutmate.domain.participation.repository;

import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation,Long> {

    boolean existsByBoardIdAndApplicantId(Long boardId, Long userId);

    Optional<Participation> findByBoardIdAndApplicantId(Long boardId, Long applicantId);

    List<Participation> findByBoardIdAndApplicant_IdIn(Long boardId, List<Long> userId);

    @Query("SELECT p.board.id, p.applicant.id FROM Participation p WHERE p.board.id IN :boardIds AND p.state = :state")
    List<Object[]> findBoardIdAndUserIdByBoardIdsAndState(@Param("boardIds") List<Long> boardId,
                                                          @Param("state") ParticipationState state);

    List<Participation> findAllByApplicant_Id(Long applicantId);

    @Query("""
        SELECT p FROM Participation p
         WHERE p.board.id = :boardId
           AND p.applicant.id = :applicantId
           AND p.isDeleted = false
    """)
    Optional<Participation> findActiveByBoardIdAndApplicantId(@Param("boardId") Long boardId,
                                                              @Param("applicantId") Long applicantId);

    // 게시글 기준 벌크 소프트 딜리트
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Participation p
           SET p.isDeleted = true,
               p.deletedAt = CURRENT_TIMESTAMP
         WHERE p.board.id = :boardId
           AND p.isDeleted = false
    """)
    int softDeleteByBoardId(@Param("boardId") Long boardId);
}
