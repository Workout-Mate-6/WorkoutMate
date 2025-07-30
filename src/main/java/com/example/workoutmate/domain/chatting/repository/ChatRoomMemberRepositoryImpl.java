package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.dto.ChatRoomResponseDto;
import com.example.workoutmate.domain.chatting.dto.QChatRoomResponseDto;
import com.example.workoutmate.domain.chatting.entity.QChatMessage;
import com.example.workoutmate.domain.chatting.entity.QChatRoom;
import com.example.workoutmate.domain.chatting.entity.QChatRoomMember;
import com.example.workoutmate.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomMemberRepositoryImpl implements ChatRoomMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<ChatRoomResponseDto> findMyChatRooms(Long myId, LocalDateTime cursor, int size) {
        QChatRoomMember cm = QChatRoomMember.chatRoomMember;
        QChatRoom c = QChatRoom.chatRoom;
        QUser sender = QUser.user;
        QUser receiver = new QUser("receiver");
        QChatMessage m = QChatMessage.chatMessage;
        QChatMessage lastMsg = new QChatMessage("lastMsg");

        BooleanBuilder condition = new BooleanBuilder()
                .and(cm.userId.eq(myId))
                .and(cm.isJoined.isTrue())
                .and(c.isDeleted.isFalse());

        // 커서 기반 페이징 조건
        if (cursor != null) {
            condition.and(
                    JPAExpressions
                            .select(m.createdAt.max())
                            .from(m)
                            .where(m.chatRoomId.eq(c.id))
                            .lt(cursor)
            );
        }

        // 최신 메시지 조회 서브쿼리
        var latestMessageSubquery = JPAExpressions
                .select(m.createdAt.max())
                .from(m)
                .where(m.chatRoomId.eq(c.id));

        return queryFactory
                .select(new QChatRoomResponseDto(
                        cm.chatRoomId,

                        new CaseBuilder()
                                .when(c.senderId.eq(myId)).then(receiver.name)
                                .otherwise(sender.name),
                        lastMsg.message,
                        lastMsg.createdAt
                ))
                .from(cm)
                .join(c).on(cm.chatRoomId.eq(c.id))
                .join(sender).on(c.senderId.eq(sender.id))
                .join(receiver).on(c.receiverId.eq(receiver.id))
                .leftJoin(lastMsg).on(
                        lastMsg.chatRoomId.eq(c.id)
                                .and(lastMsg.createdAt.eq(latestMessageSubquery))
                )
                .where(condition)
                .orderBy(c.lastChatTime.desc(), c.id.desc())
                .limit(size)
                .fetch();
    }
}
