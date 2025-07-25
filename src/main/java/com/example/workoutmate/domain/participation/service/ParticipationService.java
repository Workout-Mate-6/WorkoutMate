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

        // 중복 신청 방지
        if (participationRepository.existsByApplicantId(user.getId())) {
            throw new CustomException(CustomErrorCode.DUPLICATE_APPLICATION);
        }

        Participation participation = Participation
                .builder()
                .board(comment.getBoard())
                .comment(comment)
                .applicant(user)
                .state(ParticipationState.REQUESTED)
                .build();
        participationRepository.save(participation);
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

    public List<ParticipationAttendResponseDto> viewAttends(Long boardId, CustomUserPrincipal authUser) {
        boardSearchService.getBoardById(boardId);
        return qParticipationRepository.viewAttends(boardId);
    }
}
