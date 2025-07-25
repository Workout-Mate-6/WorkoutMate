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

import static com.example.workoutmate.domain.participation.entity.QParticipation.participation;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final QParticipationRepository qParticipationRepository;
    private final CommentService commentService;
    private final BoardSearchService boardSearchService;
    private final BoardService boardService;
    private final UserService userService;


    // 요청
    @Transactional
    public void requestApporval(Long boardId, Long commentId, CustomUserPrincipal authUser) {
        Board board = boardSearchService.getBoardById(boardId); // 게시글 존재유무 검증
        Comment comment = commentService.findById(commentId);
        User user = userService.findById(authUser.getId());
        commentService.validateCommentWriter(comment, user); // 본인이 작성한 댓글인지 검증


        // 본인이 작성한 게시글(댓글)로는 신청 못하게 하는로직
        boolean isBoardWriter = board.getWriter().getId().equals(user.getId());
        boolean isCommnetWriter = comment.getWriter().getId().equals(user.getId());
        if (isBoardWriter && isCommnetWriter) {
            throw new CustomException(CustomErrorCode.SELF_PARTICIPATION_NOT_ALLOWED);
        }

        // 테이블에서 상태 가져와서 확인
        Participation participation = participationRepository.findByBoardIdAndApplicantId(boardId, user.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.PARTICIPATION_NOT_FOUND));

        // 중복 신청 제외!
        if (participation.getState() == ParticipationState.REQUESTED) {
            throw new CustomException(CustomErrorCode.DUPLICATE_APPLICATION);
        }

        // state값 변경!
        participation.updateState(ParticipationState.REQUESTED);
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
    public List<ParticipationAttendResponseDto> viewAttends(Long boardId, CustomUserPrincipal authUser) {
        boardSearchService.getBoardById(boardId);
        return qParticipationRepository.viewAttends(boardId);
    }

    // 참여 or 불참 선택
    @Transactional
    public void chooseParticipation(
            Long commentId,
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {
        User user = userService.findById(authUser.getId());

        // 어떤 댓글로 참여신청 했는지 파악
        Participation participation = participationRepository.findByCommentIdAndApplicantId(commentId, user.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.PARTICIPATION_NOT_FOUND));

        // body로 온 값 enum으로 파싱
        ParticipationState requestedState = ParticipationState.of(participationRequestDto.getState());

        // 현재 state 값 가져오기
        ParticipationState currentState = participation.getState();

        // state 값 변경하는거 유효성 검사
        validateParticipationTransition(currentState, requestedState);

        participation.updateState(requestedState);
    }

    // state 값 변경하는거 유효성 검사
    private static void validateParticipationTransition(ParticipationState currentState, ParticipationState requestedState) {
        if (currentState == ParticipationState.DECLINED) {
            throw new CustomException(CustomErrorCode.ALLREADY_DECLINED);
        }

        if (currentState == ParticipationState.PARTICIPATION && requestedState == ParticipationState.PARTICIPATION) {
            throw new CustomException(CustomErrorCode.ALLREADY_PARTICIPATION);
        }

        if (requestedState != ParticipationState.PARTICIPATION && requestedState != ParticipationState.DECLINED) {
            throw new CustomException(CustomErrorCode.INVALID_PARTICIPATION_STATE);
        }
    }
}
