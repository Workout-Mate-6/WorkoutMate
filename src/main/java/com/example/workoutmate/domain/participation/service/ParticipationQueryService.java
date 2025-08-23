package com.example.workoutmate.domain.participation.service;

import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용
public class ParticipationQueryService {

    private final ParticipationRepository participationRepository;

    /** 유저의 참여 이력(+board fetch) */
    public List<Participation> findByApplicantId(Long userId) {
        return participationRepository.findAllByApplicant_Id(userId);
    }
}
