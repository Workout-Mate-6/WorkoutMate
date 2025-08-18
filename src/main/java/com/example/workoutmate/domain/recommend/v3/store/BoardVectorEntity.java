package com.example.workoutmate.domain.recommend.v3.store;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "board_vector")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardVectorEntity {
    @Id
    private Long boardId;

    @Lob
    @Column(nullable = false, columnDefinition = "BLOB")
    private byte[] vec;

    @Column(nullable = false)
    private Instant updatedAt;
}
