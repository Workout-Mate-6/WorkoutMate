package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.dto.ChatroomResponseDto;
import com.example.workoutmate.domain.chatting.dto.QChatroomResponseDto;
import com.example.workoutmate.domain.chatting.entity.QChatroom;
import com.example.workoutmate.domain.chatting.entity.QChatroomMember;
import com.example.workoutmate.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ChatroomMemberRepositoryImpl implements ChatroomMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<ChatroomResponseDto> findMyChatrooms(Long myId, LocalDateTime cursor, int size) {
        QChatroomMember cm = QChatroomMember.chatroomMember;
        QChatroom c = QChatroom.chatroom;
        QUser sender = new QUser("sender");
        QUser receiver = new QUser("receiver");

        BooleanBuilder condition = new BooleanBuilder()
                .and(cm.userId.eq(myId))
                .and(cm.isJoined.isTrue())
                .and(c.isDeleted.isFalse());

        if (cursor != null) {
            condition.and(c.lastChatTime.lt(cursor));
        }

        return queryFactory
                .select(new QChatroomResponseDto(
                        cm.chatroomId,

                        new CaseBuilder()
                                .when(c.senderId.eq(myId)).then(receiver.name)
                                .otherwise(sender.name),
                        c.lastMessage,
                        c.lastChatTime
                ))
                .from(cm)
                .join(c).on(cm.chatroomId.eq(c.id))
                .join(sender).on(c.senderId.eq(sender.id))
                .join(receiver).on(c.receiverId.eq(receiver.id))
                .where(condition)
                .orderBy(c.lastChatTime.desc(), c.id.desc())
                .limit(size)
                .fetch();
    }
}
