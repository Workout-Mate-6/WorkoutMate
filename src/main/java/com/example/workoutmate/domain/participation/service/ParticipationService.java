package com.example.workoutmate.domain.participation.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.enums.Status;
import com.example.workoutmate.domain.board.service.BoardSearchService;
import com.example.workoutmate.domain.board.service.BoardService;
import com.example.workoutmate.domain.participation.dto.*;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.participation.repository.ParticipationRepository;
import com.example.workoutmate.domain.participation.repository.QParticipationRepository;
import com.example.workoutmate.domain.participation.validation.ParticipationValidator;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final QParticipationRepository qParticipationRepository;
    private final BoardSearchService boardSearchService;
    private final BoardService boardService;
    private final UserService userService;
    private final ParticipationValidator participationValidator;

    // 요청
    @Transactional
    public void requestApporval(
            Long boardId,
            CustomUserPrincipal authUser
    ) {
        Board board = boardSearchService.getBoardById(boardId); // 게시글 존재유무 검증
        User user = userService.findById(authUser.getId());

        // 본인이 작성한 게시글로는 신청 못하게 하는로직
        if (board.getWriter().getId().equals(user.getId())) {
            throw new CustomException(CustomErrorCode.SELF_PARTICIPATION_NOT_ALLOWED);
        }
        if (board.getStatus().equals(Status.CLOSED)) {
            throw new CustomException(CustomErrorCode.BOARD_FULL);
        }

        // 이미 존재하는 참여여부 확인
        Optional<Participation> opt = participationRepository.findByBoardIdAndApplicantId(boardId, user.getId());

        ParticipationState requested = ParticipationState.REQUESTED;

        if (opt.isPresent()) {
            Participation participation = opt.get();
            validateStateChange(requested, participation);
            participation.updateState(requested);
        } else {
            Participation participation = Participation.builder()
                    .board(board)
                    .applicant(user)
                    .state(requested)
                    .build();
            participationRepository.save(participation);
        }
    }

    // 수락/거절
    @Transactional
    public void decideApproval(
            Long boardId,
            Long participationId,
            ParticipationRequestDto stateDto,
            CustomUserPrincipal authUser
    ) {
        Participation participation = participationRepository
                .findById(participationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PARTICIPATION_NOT_FOUND)); // 참여 내역 검증

        Board board = boardSearchService.getBoardById(boardId); // 게시글 존재 검증
        boardService.validateBoardWriter(authUser.getId(), board); // 권한 검증

        validateStateChange(stateDto, participation); // 메서드

        ParticipationState state = ParticipationState.of(stateDto.getState());

        if (state == ParticipationState.ACCEPTED) {
            if (board.getCurrentParticipants() < board.getMaxParticipants()) {
                board.increaseCurrentParticipants();
                if (board.getCurrentParticipants().equals(board.getMaxParticipants())) {
                    board.changeStatus(Status.CLOSED);
                }
            } else {
                throw new CustomException(CustomErrorCode.BOARD_FULL);
            }
        }
        participation.updateState(stateDto);
    }

    // 불참만 가능하게
    @Transactional
    public void cancelParticipation(
            Long boardId,
            CustomUserPrincipal authUser
    ) {
        User user = userService.findById(authUser.getId());

        // 게시글에 사용자가 남긴 반응(요청 찾기)
        Participation participation = participationRepository.findByBoardIdAndApplicantId(boardId, user.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.PARTICIPATION_NOT_FOUND));

        ParticipationState requested = ParticipationState.DECLINED;
        validateStateChange(requested, participation); // 메서드


        // 락 적용
        Board board = boardService.findByIdWithPessimisticLock(boardId);

        // 불참으로 변경시 -1 하는 로직
        if (participation.getState() == ParticipationState.ACCEPTED) {
            board.decreaseCurrentParticipants();
            if (board.getStatus().equals(Status.CLOSED)) {
                board.changeStatus(Status.OPEN);
            }
        }

        participation.updateState(requested);
    }

    ////////////////////////// 반응 조회 //////////////////////////
    public Page<ParticipationByBoardResponseDto> viewApprovalsForMyBoards(
            int page, int size,
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return qParticipationRepository.viewApprovalsForWriter(pageable, participationRequestDto, authUser);
    }
    // 신청자 관점
    public Page<MyApplicationResponseDto> viewMyApplications(
            int page, int size,
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return qParticipationRepository.viewApplicationsForApplicant(pageable, participationRequestDto, authUser);
    }
    // 두 섹션을 묶어서 반환
    public CombinedParticipationViewResponse viewApprovalCombined(
            int writerPage, int writerSize,
            int applicantPage, int applicantSize,
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {
        Page<ParticipationByBoardResponseDto> writerSide =
                viewApprovalsForMyBoards(writerPage, writerSize, participationRequestDto, authUser);

        Page<MyApplicationResponseDto> applicantSide =
                viewMyApplications(applicantPage, applicantSize, participationRequestDto, authUser);

        return new CombinedParticipationViewResponse(writerSide, applicantSide);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////


    // 파티 조회
    public List<ParticipationAttendResponseDto> viewAttends(Long boardId) {
        boardSearchService.getBoardById(boardId);
        return qParticipationRepository.viewAttends(boardId);
    }


    // 상태 변경 요청에 대한 검증 메서드
    private void validateStateChange(ParticipationState requestedState, Participation participation) {
        // 현재 state 값 가져오기
        ParticipationState currentState = participation.getState();

        participationValidator.validateAlreadyHandled(currentState, requestedState); // 같은 요청 두번 방지
        participationValidator.validateParticipationTransition(currentState, requestedState); // 잘못된 상태 변경 방지
    }

    // 수락 | 거절 부분에서 사용
    private void validateStateChange(ParticipationRequestDto dto, Participation p) {
        validateStateChange(ParticipationState.of(dto.getState()), p);
    }


    public List<Participation> findByApplicant_Id(Long userId) {
        return participationRepository.findAllByApplicant_Id(userId);
    }
}
