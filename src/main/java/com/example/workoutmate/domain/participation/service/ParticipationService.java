package com.example.workoutmate.domain.participation.service;

import com.example.workoutmate.domain.board.service.BoardSearchService;
import com.example.workoutmate.domain.comment.entity.Comment;
import com.example.workoutmate.domain.comment.service.CommentService;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.participation.repository.ParticipationRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final CommentService commentService;
    private final BoardSearchService boardSearchService;
    private final UserService userService;


    @Transactional
    public void requestApporval(Long boardId, Long commentId, CustomUserPrincipal authUser) {
        boardSearchService.getBoardById(boardId); // 게시글 존재유무 검증
        Comment comment = commentService.findById(commentId);
        User user = userService.findById(authUser.getId());
        commentService.validateCommentWriter(comment, user); // 본인이 작성한 댓글인지 검증

        if (participationRepository.existsByApplicantId(user.getId())) {
            throw new CustomException(CustomErrorCode.DUPLICATE_APPLICATION);
        }

        Participation participation = Participation.builder().board(comment.getBoard()).comment(comment).applicant(user).state(ParticipationState.REQUESTED).build();
        participationRepository.save(participation);
    }
}
