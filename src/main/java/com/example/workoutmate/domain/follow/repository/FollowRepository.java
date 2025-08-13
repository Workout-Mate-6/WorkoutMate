package com.example.workoutmate.domain.follow.repository;

import com.example.workoutmate.domain.follow.entity.Follow;
import com.example.workoutmate.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);

    // 게시글 부분
    @Query("select f.following.id from Follow f where f.follower.id = :userId")
    List<Long> findFollowingUserIds(@Param("userId") Long userId);

    void deleteByfollowerIdAndFollowingId(Long id, Long userId);

    boolean existsByFollowerIdAndFollowingId(Long id, Long userId);


    List<Follow> findByFollower_Id(Long followerId);
}
