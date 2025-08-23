package com.example.workoutmate.domain.board.entity;

import com.example.workoutmate.domain.board.enums.Status;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.global.entity.BaseEntity;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board",
        indexes = {
                @Index(name = "idx_board_list", columnList = "is_deleted, modified_at DESC, id DESC"),
                @Index(name = "idx_board_writer_feed", columnList = "writer_id, is_deleted, created_at DESC, id DESC"),
                @Index(name = "idx_board_category_feed", columnList = "sport_type, is_deleted, modified_at DESC, id DESC")
        }
)
public class Board extends BaseEntity {

    // 게시글 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
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

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    // 삭제일
    @Column
    private LocalDateTime deletedAt;

    // 모집 인원
    @Column(name = "max_participants", nullable = false)
    private Long maxParticipants;

    // 모집된 인원
    @Column(name = "current_participants")
    @Builder.Default
    private Long currentParticipants = 0L;

    // 모집 상태
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    // 운동 할 시간
    @Column(nullable = false)
    private LocalDateTime startTime;


    @Builder
    public Board(User writer, String title, String content, SportType sportType, Long maxParticipants) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.sportType = sportType;
        this.maxParticipants = maxParticipants;
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public void update(String title, String content, SportType sportType, Long maxParticipants) {
        this.title = title;
        this.content = content;
        this.sportType = sportType;
        this.maxParticipants = maxParticipants;
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }


    public void increaseCurrentParticipants() {
        if (this.currentParticipants >= this.maxParticipants) {
            throw new CustomException(CustomErrorCode.BOARD_FULL);
        }
        this.currentParticipants++;
    }

    public void decreaseCurrentParticipants() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    public void changeStatus(Status status) {
        if (this.status != status) {
            this.status = status;
        }
    }

    public void increaseViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void updateCurrentParticipants(long count) {
        if (count < 0) {
            throw new IllegalArgumentException("참여자 수는 음수일 수 없습니다.");
        }
        if (count > this.maxParticipants) {
            throw new CustomException(CustomErrorCode.BOARD_FULL);
        }
        this.currentParticipants = count;
    }
}
