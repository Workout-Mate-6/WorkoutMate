package com.example.workoutmate.domain.participation.repository;

import com.example.workoutmate.domain.participation.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation,Long> {
    boolean existsByApplicantId(Long id);
}
