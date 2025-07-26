package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.dto.ChatRoomResponseDto;
import com.example.workoutmate.domain.chatting.dto.QChatRoomResponseDto;
import com.example.workoutmate.domain.chatting.entity.QChatRoom;
import com.example.workoutmate.domain.chatting.entity.QChatRoomMember;
import com.example.workoutmate.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ChatRoomMemberRepositoryImpl implements ChatRoomMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<ChatRoomResponseDto> findMyChatRooms(Long myId, LocalDateTime cursor, int size) {
        QChatRoomMember cm = QChatRoomMember.chatRoomMember;
        QChatRoom c = QChatRoom.chatRoom;
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
                .select(new QChatRoomResponseDto(
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
