package com.example.workoutmate.domain.recommend.v3.store;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_vector")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVectorEntity {
    @Id
    private Long userId;

    @Lob
    @Column(nullable = false, columnDefinition = "BLOB")
    private byte[] vec; // float[] 직렬화

    @Column(nullable = false)
    private Instant updatedAt;
}
