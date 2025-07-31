package com.example.workoutmate.domain.participation.entity;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.comment.entity.Comment;
import com.example.workoutmate.domain.participation.dto.ParticipationRequestDto;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participation", uniqueConstraints = {@UniqueConstraint(name = "uq_participation_comment_user",columnNames = {"applicant_id"})}) // 신청 중복 제거
public class Participation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ParticipationState state = ParticipationState.NONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    // 요청쪽에서 사용
    public void updateState(ParticipationState state) {
        this.state = state;
    }

    // 수락/거절쪽에서 사용
    public void updateState(ParticipationRequestDto participationRequestDto) {
        this.state = ParticipationState.of(participationRequestDto.getState());
    }
}
