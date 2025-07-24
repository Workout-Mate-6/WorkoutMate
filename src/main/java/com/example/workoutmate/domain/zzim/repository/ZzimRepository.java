package com.example.workoutmate.domain.zzim.repository;

import com.example.workoutmate.domain.zzim.entity.Zzim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZzimRepository extends JpaRepository<Zzim, Long> {
}
