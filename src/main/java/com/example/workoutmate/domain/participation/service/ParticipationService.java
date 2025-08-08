package com.example.workoutmate.domain.participation.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.enums.Status;
import com.example.workoutmate.domain.board.service.BoardSearchService;
import com.example.workoutmate.domain.board.service.BoardService;
import com.example.workoutmate.domain.comment.service.CommentService;
import com.example.workoutmate.domain.participation.dto.ParticipationAttendResponseDto;
import com.example.workoutmate.domain.participation.dto.ParticipationByBoardResponseDto;
import com.example.workoutmate.domain.participation.dto.ParticipationRequestDto;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {
        Board board = boardSearchService.getBoardById(boardId); // 게시글 존재유무 검증
        User user = userService.findById(authUser.getId());

        // 본인이 작성한 게시글로는 신청 못하게 하는로직
        if (board.getWriter().getId().equals(user.getId())) {
            throw new CustomException(CustomErrorCode.SELF_PARTICIPATION_NOT_ALLOWED);
        }

        // 타입 변환...
        ParticipationState state = ParticipationState.of(participationRequestDto.getState());
        // 상태 검사 (신청만 허용)
        if (!validStateRequested.contains(state)) {
            throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION); // 이거 잘못된 요청이라는 걸로 수정
        }

        // 게시글에 요청이 있는지 없는지 확인
        Optional<Participation> optionalParticipation =
                participationRepository.findByBoardIdAndApplicantId(boardId, user.getId());

        Participation participation;

        if (board.getStatus().equals(Status.CLOSED)) {
            throw new CustomException(CustomErrorCode.BOARD_FULL);
        }

        // 값이 있는지 없는지 확인
        if (optionalParticipation.isPresent()) { // 있으면
            // 댓글로 인해 참여신청을 했는지에 대해서 확인
            participation = optionalParticipation.get();

            // 현재 state 값 가져오기
            validateStateChange(participationRequestDto, participation);
            // state값 변경!
            participation.updateState(participationRequestDto);
        } else { // 없으면
            participation = Participation.builder().board(board).applicant(user).state(state).build();
            participationRepository.save(participation);
        }
    }

    // 수락/거절
    @Transactional
    public void decideApproval(
            Long boardId,
            Long participationId,
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {
        Participation participation = participationRepository
                .findById(participationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PARTICIPATION_NOT_FOUND)); // 참여 내역 검증

        Board board = boardSearchService.getBoardById(boardId); // 게시글 존재 검증
        boardService.validateBoardWriter(authUser.getId(), board); // 권한 검증

        validateStateChange(participationRequestDto, participation); // 메서드

        ParticipationState state = ParticipationState.of(participationRequestDto.getState());

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
        participation.updateState(participationRequestDto);
    }

     // 불참만 가능하게
    @Transactional
    public void cancelParticipation(
            Long boardId,
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {
        User user = userService.findById(authUser.getId());

        // 게시글에 사용자가 남긴 반응(요청 찾기)
        Participation participation = participationRepository.findByBoardIdAndApplicantId(boardId, user.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.PARTICIPATION_NOT_FOUND));

        validateStateChange(participationRequestDto, participation); // 메서드


        // 타입 변환...
        ParticipationState state = ParticipationState.of(participationRequestDto.getState());
        // 상태 검사 (불참만 허용)
        if (!validStateDecline.contains(state)) {
            throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION); // 이거 잘못된 요청이라는 걸로 수정
        }

        // 참가 인원 카운트 하는 로직
        boolean isChoosingToDecline = (state == ParticipationState.DECLINED);
        // 락 적용
        Board board = boardService.findByIdWithPessimisticLock(boardId);

        // 불참으로 변경시 -1 하는 로직
        if (isChoosingToDecline && participation.getState() == ParticipationState.ACCEPTED) {
            board.decreaseCurrentParticipants();
            if (board.getStatus().equals(Status.CLOSED)) {
                board.changeStatus(Status.OPEN);
            }
        }

        participation.updateState(participationRequestDto);
    }

    // 신청자 조회
    public Page<ParticipationByBoardResponseDto> viewApproval(
            int page,
            int size,
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<ParticipationByBoardResponseDto> result =
                qParticipationRepository.viewApproval(pageable, participationRequestDto, authUser);
        if (result == null || result.isEmpty()) {
            throw new CustomException(CustomErrorCode.USER_RECEIVED_REQUEST_NOT_FOUND);
        }
        return result;
    }

    // 파티 조회
    public List<ParticipationAttendResponseDto> viewAttends(Long boardId) {
        boardSearchService.getBoardById(boardId);
        return qParticipationRepository.viewAttends(boardId);
    }


    // 상태 변경 요청에 대한 검증 메서드
    private void validateStateChange(ParticipationRequestDto participationRequestDto, Participation participation) {
        // 현재 state 값 가져오기
        ParticipationState currentState = participation.getState();
        // body로 온 값 enum으로 파싱
        ParticipationState requestedState = ParticipationState.of(participationRequestDto.getState());

        participationValidator.validateAlreadyHandled(currentState, requestedState); // 같은 요청 두번 방지
        participationValidator.validateParticipationTransition(currentState, requestedState); // 잘못된 상태 변경 방지
    }

    // 허용치 범위 설정! (신청만 허용)
    Set<ParticipationState> validStateRequested = Set.of(
            ParticipationState.REQUESTED
    );

    // 불참만 가능
    Set<ParticipationState> validStateDecline = Set.of(
            ParticipationState.DECLINED
    );


}
