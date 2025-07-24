package com.example.workoutmate.domain.zzim.entity;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "zzim", uniqueConstraints = {@UniqueConstraint(columnNames = {"board_id", "user_id"})})
public class Zzim extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 찜한 게시글
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 찜한 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 정적 팩토리 메서드
    public static Zzim of(Board board, User user) {

        return Zzim.builder()
                .board(board)
                .user(user)
                .build();
    }
}
