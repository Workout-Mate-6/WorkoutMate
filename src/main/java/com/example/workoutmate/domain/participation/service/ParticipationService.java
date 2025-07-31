package com.example.workoutmate.domain.participation.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.service.BoardSearchService;
import com.example.workoutmate.domain.board.service.BoardService;
import com.example.workoutmate.domain.comment.entity.Comment;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final QParticipationRepository qParticipationRepository;
    private final CommentService commentService;
    private final BoardSearchService boardSearchService;
    private final BoardService boardService;
    private final UserService userService;
    private final ParticipationValidator participationValidator;


    // 요청
    @Transactional
    public void requestApporval(
            Long boardId,
            Long commentId,
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {

        Board board = boardSearchService.getBoardById(boardId); // 게시글 존재유무 검증
        Comment comment = commentService.findById(commentId);
        User user = userService.findById(authUser.getId());
        commentService.validateCommentWriter(comment, user); // 본인이 작성한 댓글인지 검증


        // 본인이 작성한 게시글(댓글)로는 신청 못하게 하는로직
        boolean isBoardWriter = board.getWriter().getId().equals(user.getId());
        boolean isCommentWriter = comment.getWriter().getId().equals(user.getId());
        if (isBoardWriter && isCommentWriter) {
            throw new CustomException(CustomErrorCode.SELF_PARTICIPATION_NOT_ALLOWED);
        }

        // 테이블에서 상태 가져와서 확인
        Participation participation = participationRepository.findByBoardIdAndApplicantId(boardId, user.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.PARTICIPATION_NOT_FOUND));

        // 타입 변환...
        ParticipationState state = ParticipationState.of(participationRequestDto.getState());
        // 상태 검사 (신청만 허용)
        if (!validState.contains(state)) {
            throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION); // 이거 잘못된 요청이라는 걸로 수정
        }
        // 현재 state 값 가져오기
        validateStateChange(participationRequestDto, participation);

        // state값 변경!
        participation.updateState(participationRequestDto);
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

        participation.updateState(participationRequestDto);
    }

    // 참여 or 불참 선택
    @Transactional
    public void chooseParticipation(
            Long boardId,
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {
        User user = userService.findById(authUser.getId());

        // 게시글에 사용자가 남긴 반응(요청 찾기)
        Participation participation = participationRepository.findByBoardIdAndApplicantId(boardId, user.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.PARTICIPATION_NOT_FOUND));

        validateStateChange(participationRequestDto, participation); // 메서드

        // 참가 인원 카운트 하는 로직
        ParticipationState state = ParticipationState.of(participationRequestDto.getState());
        boolean isChoosingToParticipation = (state == ParticipationState.PARTICIPATION);
        boolean isChoosingToDecline = (state == ParticipationState.DECLINED);
        boolean isAccepted = participation.getState() == ParticipationState.ACCEPTED;
        // 락 적용
        Board board = boardService.findByIdWithPessimisticLock(boardId);

        // 참여 라고 했을때 +1 하는 로직
        if (isChoosingToParticipation && isAccepted) {
            board.increaseCurrentParticipants();
        }

        // 불참으로 변경시 -1 하는 로직
        if (isChoosingToDecline && participation.getState() == ParticipationState.PARTICIPATION) {
            board.decreaseCurrentParticipants();
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
    Set<ParticipationState> validState = Set.of(
            ParticipationState.REQUESTED
    );


}
