package com.example.workoutmate.domain.participation.repository;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.QBoard;
import com.example.workoutmate.domain.participation.dto.*;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.entity.QParticipation;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.user.entity.QUser;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.workoutmate.domain.participation.entity.QParticipation.participation;
import static com.example.workoutmate.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QParticipationRepository {

    private final JPAQueryFactory queryFactory;


    private BooleanBuilder buildCommonFilter(
            ParticipationRequestDto dto,
            QParticipation participation
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        if (dto != null && dto.getState() != null && !dto.getState().isEmpty()) {
            builder.and(participation.state.eq(ParticipationState.valueOf(dto.getState())));
        }
        return builder;
    }

    // A) 내가 쓴 게시글에 달린 요청들 (게시글 단위 그룹핑 DTO)
    public Page<ParticipationByBoardResponseDto> viewApprovalsForWriter(
            Pageable pageable,
            ParticipationRequestDto dto,
            CustomUserPrincipal authUser
    ) {
        QParticipation p = QParticipation.participation;
        QBoard b = QBoard.board;
        QUser u = QUser.user;

        BooleanBuilder builder = buildCommonFilter(dto, p);
        builder.and(b.writer.id.eq(authUser.getId()));

        long total = Optional.ofNullable(
                queryFactory.select(p.count())
                        .from(p)
                        .join(p.board, b)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        List<Participation> rows = queryFactory
                .selectFrom(p)
                .join(p.board, b).fetchJoin()
                .join(p.applicant, u).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(p.createdAt.desc())
                .fetch();

        Map<Board, List<ParticipationResponseDto>> grouped = rows.stream()
                .collect(Collectors.groupingBy(
                        Participation::getBoard,
                        Collectors.mapping(pp -> new ParticipationResponseDto(
                                pp.getId(),
                                pp.getApplicant().getName(),
                                pp.getState().toString(),
                                pp.getCreatedAt()
                        ), Collectors.toList())
                ));

        List<ParticipationByBoardResponseDto> content = grouped.entrySet().stream()
                .map(e -> new ParticipationByBoardResponseDto(
                        e.getKey().getId(),
                        e.getKey().getTitle(),
                        e.getValue()
                ))
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    // B) 내가 신청한 목록(그룹핑 없음 단건 리스트)
    public Page<MyApplicationResponseDto> viewApplicationsForApplicant(
            Pageable pageable,
            ParticipationRequestDto dto,
            CustomUserPrincipal authUser
    ) {
        QParticipation p = QParticipation.participation;
        QBoard b = QBoard.board;
        QUser writer = new QUser("writer"); // 게시글 작성자 alias
        QUser applicant = QUser.user;

        BooleanBuilder builder = buildCommonFilter(dto, p);
        builder.and(p.applicant.id.eq(authUser.getId()));

        long total = Optional.ofNullable(
                queryFactory.select(p.count())
                        .from(p)
                        .join(p.board, b)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        // 작성자 정보도 같이
        List<Tuple> rows = queryFactory
                .select(p.id, b.id, b.title, writer.id, writer.name, p.state, p.createdAt)
                .from(p)
                .join(p.board, b)
                .join(b.writer, writer)
                .join(p.applicant, applicant)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(p.createdAt.desc())
                .fetch();

        List<MyApplicationResponseDto> content = rows.stream()
                .map(t -> new MyApplicationResponseDto(
                        t.get(p.id),
                        t.get(b.id),
                        t.get(b.title),
                        t.get(writer.id),
                        t.get(writer.name),
                        t.get(p.state).toString(),
                        t.get(p.createdAt)
                ))
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    public List<ParticipationAttendResponseDto> viewAttends(Long boardId) {
        return queryFactory.select(Projections.constructor(
                        ParticipationAttendResponseDto.class,
                        participation.applicant.name,
                        participation.state.stringValue()
                ))
                .from(participation)
                .join(participation.applicant, user)
                .where(
                        participation.board.id.eq(boardId),
                        participation.state.eq(ParticipationState.ACCEPTED)
                )
                .fetch();
    }

    // 게시글 IDs별 참여자 집합
    public Map<Long, Set<Long>> findParticipantIdsByBoardIds(List<Long> boardIds, ParticipationState state) {
        QParticipation p = QParticipation.participation;
        List<Tuple> rows = queryFactory
                .select(p.board.id, p.applicant.id)
                .from(p)
                .where(p.board.id.in(boardIds),
                        p.state.eq(state))
                .fetch();

        Map<Long, Set<Long>> map = new HashMap<>();
        for (Tuple t : rows) {
            Long bId = t.get(p.board.id);
            Long uId = t.get(p.applicant.id);
            map.computeIfAbsent(bId, k -> new HashSet<>()).add(uId);
        }
        return map;
    }

    // 최근 from(일자) 이후, 특정 사용자들(친구들)의 참여 종목 집합
    public Set<String> findSportTypesByApplicantsAndDate(Set<Long> applicantIds, LocalDate from, ParticipationState state) {
        if (applicantIds == null || applicantIds.isEmpty()) return Set.of();
        QParticipation p = QParticipation.participation;
        QBoard b = QBoard.board;
        List<String> types = queryFactory
                .select(b.sportType.stringValue())
                .from(p)
                .join(p.board, b)
                .where(p.applicant.id.in(applicantIds),
                        p.state.eq(state),
                        p.createdAt.goe(from.atStartOfDay()))  // createdAt 필드명 프로젝트에 맞춰 변경
                .distinct()
                .fetch();
        return new HashSet<>(types);
    }
}
