package com.example.workoutmate.domain.participation.repository;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.QBoard;
import com.example.workoutmate.domain.participation.dto.ParticipationAttendResponseDto;
import com.example.workoutmate.domain.participation.dto.ParticipationByBoardResponseDto;
import com.example.workoutmate.domain.participation.dto.ParticipationRequestDto;
import com.example.workoutmate.domain.participation.dto.ParticipationResponseDto;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.entity.QParticipation;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.user.entity.QUser;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.workoutmate.domain.participation.entity.QParticipation.participation;
import static com.example.workoutmate.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QParticipationRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 로그인한 사용자가 작성한 게시글에 달린 참여 요청들을 조회합니다.
     * - 참여 상태로 필터링 가능
     * - 게시글 기준으로 참여 요청들을 그룹핑
     * - 페이징 처리된 결과 반환
     */
    public Page<ParticipationByBoardResponseDto> viewApproval(
            Pageable pageable,
            ParticipationRequestDto participationRequestDto,
            CustomUserPrincipal authUser
    ) {
        QParticipation participation = QParticipation.participation;
        QBoard board = QBoard.board;
        QUser user = QUser.user;

        // 로그인한 사용자가 작성한 게시글의 참여 요청만 조회
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(board.writer.id.eq(authUser.getId()));

        // 필터
        if (participationRequestDto != null &&
                participationRequestDto.getState() != null &&
                !participationRequestDto.getState().isEmpty()
        ) {
            builder.and(participation.state.eq(
                    ParticipationState.valueOf(participationRequestDto.getState())
            ));
        }

        // 전체 갯수 세기 (페이징용)
        long totalCount = Optional.ofNullable(
                queryFactory
                        .select(participation.count())
                        .from(participation)
                        .join(participation.board, board)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        // 참여요청한 데이터 조회
        List<Participation> content = queryFactory
                .selectFrom(participation)
                .join(participation.board, board).fetchJoin()
                .join(participation.applicant, user).fetchJoin() // 신청자 정보를 조회
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(participation.createdAt.desc())
                .fetch();

        // Participation → DTO 변환 (게시글 기준 그룹핑)
        Map<Board, List<ParticipationResponseDto>> grouped = content.stream()
                .collect(Collectors.groupingBy(
                        Participation::getBoard,
                        Collectors.mapping(
                                p -> new ParticipationResponseDto(
                                        p.getId(),
                                        p.getApplicant().getName(), // 신청자 이름으로 변경
                                        p.getState().toString(),
                                        p.getCreatedAt()
                                ),
                                Collectors.toList()
                        )
                ));

        // 게시글 별로 들어온 요청모아서 최종 DTO로 바꾸기
        List<ParticipationByBoardResponseDto> dtoList = grouped.entrySet().stream()
                .map(e -> new ParticipationByBoardResponseDto(
                        e.getKey().getId(),
                        e.getKey().getTitle(),
                        e.getValue() // 게시글에 달린 참여 요청 DTO 리스트
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, totalCount);
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
}
