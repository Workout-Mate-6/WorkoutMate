package com.example.workoutmate.domain.participation.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.participation.repository.ParticipationRepository;
import com.example.workoutmate.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationCreateService {

    private final ParticipationRepository participationRepository;
    // 참여자(대기) 매서드
    @Transactional
    public void participationInjector(Board board, User user) {
        // 작성자 제외!
        if (user.getId().equals(board.getWriter().getId())) {
            return;
        }
        // 이미 테이블에 있으면 제외
        boolean alreadyExists = participationRepository.existsByBoardIdAndApplicantId(board.getId(), user.getId());
        if (alreadyExists) {
            return;
        }

        Participation participation = Participation.builder()
                .board(board)
                .applicant(user)
                .state(ParticipationState.NONE)
                .build();

        participationRepository.save(participation);
    }

    @Transactional(readOnly = true)
    public Map<Long, ParticipationState> getParticipationStatus(Long boardId, List<Long> userId) {
        List<Participation> participations = participationRepository
                .findByBoardIdAndApplicant_IdIn(boardId, userId);

        return participations.stream()
                .collect(Collectors.toMap(
                        participation -> participation.getApplicant().getId(),
                        Participation::getState
                ));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public int softDeleteByBoardId(Long boardId) {
        return participationRepository.softDeleteByBoardId(boardId);
    }
}
