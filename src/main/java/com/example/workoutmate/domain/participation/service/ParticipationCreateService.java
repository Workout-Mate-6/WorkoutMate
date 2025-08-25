package com.example.workoutmate.domain.participation.service;

import com.example.workoutmate.domain.participation.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipationCreateService {

    private final ParticipationRepository participationRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public int softDeleteByBoardId(Long boardId) {
        return participationRepository.softDeleteByBoardId(boardId);
    }
}
