package com.example.workoutmate.domain.participation.entity;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.participation.dto.ParticipationRequestDto;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "participation",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_participation_board_user_active",
                        columnNames = {"board_id", "applicant_id", "is_deleted"})
        }
)
// 신청 중복 제거
@SQLDelete(sql = """
            UPDATE participation
               SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP
             WHERE id = ?
        """)
@Where(clause = "is_deleted = false")
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
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;


    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;


    public void updateState(ParticipationState newState) {
        this.state = newState;
    }

    public void updateState(ParticipationRequestDto dto) {
        updateState(ParticipationState.of(dto.getState()));
    }
}
