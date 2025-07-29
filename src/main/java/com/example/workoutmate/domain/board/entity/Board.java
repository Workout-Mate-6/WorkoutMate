package com.example.workoutmate.domain.board.entity;

import com.example.workoutmate.domain.board.enums.Status;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
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
    @Builder.Default
    private Boolean isDeleted = false;

    // 삭제일
    @Column
    private LocalDateTime deletedAt;

    // 모집 인원
    @Column(name = "target_count", nullable = false)
    private Long targetCount;

    // 모집된 인원
    @Column(name = "current_count")
    @Builder.Default
    private Long currentCount = 0L;

    // 모집 상태
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;


    @Builder
    public Board(User writer, String title, String content, SportType sportType, Long targetCount) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.sportType = sportType;
        this.targetCount = targetCount;
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public void update(String title, String content, SportType sportType, Long targetCount) {
        this.title = title;
        this.content = content;
        this.sportType = sportType;
        this.targetCount = targetCount;
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

}
