package com.example.workoutmate.domain.user.repository;

import com.example.workoutmate.domain.user.dto.UserInfoResponseDto;
import com.example.workoutmate.domain.user.entity.QUser;
import com.example.workoutmate.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QUser user = QUser.user;

    @Override
    public UserInfoResponseDto getUserInfo() {
//        List<User> content = queryFactory
//                .selectFrom(user)
//                .where(
//
//                )
        return null;
    }
}
