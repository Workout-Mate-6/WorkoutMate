package com.example.workoutmate.domain.follow;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class FollowBulkInsertTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("팔로워 1000명 팔로잉 500명 더미 데이터 생성")
    void bulkTest() {
        int followerBatchSize = 500;
        int followingBatchSize = 500;

        // 1. 기준 유저(나) 1명 미리 생성
        jdbcTemplate.update(
                "INSERT INTO user (email, password, name, gender, role, is_deleted, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())",
                "me@email.com", "password", "me", "Male", "GUEST", false
        );

        // 기준 유저의 id 가져오기 (auto_increment 기준)
        Long meId = jdbcTemplate.queryForObject("SELECT id FROM user WHERE email = ?", Long.class, "me@email.com");

        // 2. 팔로워 1000명 (나를 팔로우하는 사람)
        List<Object[]> followerParams = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            String email = "follower" + i + "@email.com";
            String name = "follower" + i;
            followerParams.add(new Object[]{email, "password", name, "Male", "GUEST", false});
            if (followerParams.size() == followerBatchSize || i == 1000) {
                jdbcTemplate.batchUpdate(
                        "INSERT INTO user (email, password, name, gender, role, is_deleted, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())",
                        followerParams
                );
                followerParams.clear();
            }
        }

        // 팔로워들의 id 가져오기
        List<Long> followerIds = jdbcTemplate.queryForList(
                "SELECT id FROM user WHERE email LIKE 'follower%@email.com'",
                Long.class
        );

        // Follow insert (팔로워 -> 나)
        List<Object[]> followFollowerParams = new ArrayList<>();
        for (Long followerId : followerIds) {
            followFollowerParams.add(new Object[]{followerId, meId});
            if (followFollowerParams.size() == followerBatchSize) {
                jdbcTemplate.batchUpdate(
                        "INSERT INTO follows (follower_id, following_id, created_at, modified_at) VALUES (?, ?, NOW(), NOW())",
                        followFollowerParams
                );
                followFollowerParams.clear();
            }
        }
        if (!followFollowerParams.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO follows (follower_id, following_id, created_at, modified_at) VALUES (?, ?, NOW(), NOW())",
                    followFollowerParams
            );
        }

        // 3. 팔로잉 500명 (내가 팔로우하는 사람)
        List<Object[]> followingParams = new ArrayList<>();
        for (int i = 1; i <= 500; i++) {
            String email = "following" + i + "@email.com";
            String name = "following" + i;
            followingParams.add(new Object[]{email, "password", name, "Male", "GUEST", false});
            if (followingParams.size() == followingBatchSize || i == 500) {
                jdbcTemplate.batchUpdate(
                        "INSERT INTO user (email, password, name, gender, role, is_deleted, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())",
                        followingParams
                );
                followingParams.clear();
            }
        }

        // 팔로잉들의 id 가져오기
        List<Long> followingIds = jdbcTemplate.queryForList(
                "SELECT id FROM user WHERE email LIKE 'following%@email.com'",
                Long.class
        );

        // Follow insert (나 -> 팔로잉)
        List<Object[]> followFollowingParams = new ArrayList<>();
        for (Long followingId : followingIds) {
            followFollowingParams.add(new Object[]{meId, followingId});
            if (followFollowingParams.size() == followingBatchSize) {
                jdbcTemplate.batchUpdate(
                        "INSERT INTO follows (follower_id, following_id, created_at, modified_at) VALUES (?, ?, NOW(), NOW())",
                        followFollowingParams
                );
                followFollowingParams.clear();
            }
        }
        if (!followFollowingParams.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO follow (follower_id, following_id, created_at, modified_at) VALUES (?, ?, NOW(), NOW())",
                    followFollowingParams
            );
        }
    }
}
