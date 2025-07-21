package com.example.workoutmate.domain.board.entity;

import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class Board extends BaseEntity {

    // 게시글 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    // 제목
    @Column(nullable = false)
    private String title;

    // 내용
    @Column(nullable = false)
    private String content;

    // 운동 종목 (Enum 형식)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    // 삭제 여부
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // 삭제일
    @Column
    private LocalDateTime deletedAt;

}
