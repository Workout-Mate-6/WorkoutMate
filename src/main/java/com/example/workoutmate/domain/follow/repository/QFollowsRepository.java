package com.example.workoutmate.domain.follow.repository;


import com.example.workoutmate.domain.follow.dto.FollowsResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.workoutmate.domain.follow.entity.QFollow.follow;

@Repository
@RequiredArgsConstructor
public class QFollowsRepository {

    private final JPAQueryFactory queryFactory;


    public List<FollowsResponseDto> viewFollower(Long userId, Integer size, Long cursor) {

        BooleanExpression cursorPredicate = (cursor != null)
                ? follow.id.lt(cursor)
                : null;

        return queryFactory.select(Projections.constructor(FollowsResponseDto.class,
                follow.follower.id,
                follow.follower.name
        ))
                .from(follow).where(follow.following.id.eq(userId),cursorPredicate)
                .orderBy(follow.id.desc())
                .limit(size)
                .fetch();
    }

    public List<FollowsResponseDto> viewFollowing(Long userId, Integer size, Long cursor) {
        BooleanExpression cursorPredicate = (cursor != null)
                ? follow.id.lt(cursor)
                : null;

        return queryFactory.select(Projections.constructor(FollowsResponseDto.class,
                follow.following.id,
                follow.following.name
        ))
                .from(follow).where(follow.follower.id.eq(userId),cursorPredicate)
                .orderBy(follow.id.desc())
                .limit(size)
                .fetch();
    }
}
