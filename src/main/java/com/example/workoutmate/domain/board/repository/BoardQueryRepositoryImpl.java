package com.example.workoutmate.domain.board.repository;

import com.example.workoutmate.domain.board.dto.BoardFilterRequestDto;
import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.QBoard;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.follow.entity.QFollow;
import com.example.workoutmate.domain.user.entity.QUser;
import com.example.workoutmate.domain.zzim.entity.QZzim;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Board> searchWithFilters(Long userId, BoardFilterRequestDto filter, Pageable pageable) {

        QBoard board = QBoard.board;
        QUser writer = QUser.user;

        // 조건
        BooleanExpression condition = board.isDeleted.eq(false)
                .and(eqSportType(filter.getSportType()))
                .and(eqMyPosts(userId, filter.getOnlyMyPosts()))
                .and(eqFollowing(userId, filter.getOnlyFollowing()))
                .and(eqZzimmed(userId, filter.getOnlyZzimmed()));

        // content 조회 쿼리
        List<Board> content = queryFactory
                .selectFrom(board)
                .leftJoin(board.writer, writer).fetchJoin()
                .where(condition)
                .orderBy(board.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        // total count 쿼리 (fetchJoin 제거)
        Long total = queryFactory
                .select(board.count())
                .from(board)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression eqSportType(SportType sportType) {
        return sportType != null ? QBoard.board.sportType.eq(sportType) : null;
    }

    private BooleanExpression eqMyPosts(Long userId, Boolean onlyMyPosts) {
        return onlyMyPosts != null && onlyMyPosts ? QBoard.board.writer.id.eq(userId) : null;
    }

    private BooleanExpression eqFollowing(Long userId, Boolean onlyFollowing) {
        if (onlyFollowing != null && onlyFollowing) {
            QFollow follow = QFollow.follow;
            List<Long> followingIds = queryFactory
                    .select(follow.following.id)
                    .from(follow)
                    .where(follow.follower.id.eq(userId))
                    .fetch();

            if (followingIds.isEmpty()) {
                return QBoard.board.id.isNull(); // 결과가 없도록
            }

            return QBoard.board.writer.id.in(followingIds);
        }
        return null;
    }

    private BooleanExpression eqZzimmed(Long userId, Boolean onlyZzimmed) {
        if (onlyZzimmed != null && onlyZzimmed) {
            QZzim zzim = QZzim.zzim;

            List<Long> boardIds = queryFactory
                    .select(zzim.board.id)
                    .from(zzim)
                    .where(zzim.user.id.eq(userId))
                    .fetch();

            if (boardIds.isEmpty()) {
                return QBoard.board.id.isNull();
            }

            return QBoard.board.id.in(boardIds);
        }
        return null;
    }
}
