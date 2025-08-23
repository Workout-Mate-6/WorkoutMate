package com.example.workoutmate.domain.follow.entity;

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
@Table(name = "follows")
@NoArgsConstructor
@AllArgsConstructor
public class Follow extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="following_id")
    private User following;

    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
    }
}
