package com.example.workoutmate.domain.chatting.repository;

import com.example.workoutmate.domain.chatting.entity.ChatMessage;
import com.example.workoutmate.domain.chatting.entity.QChatMessage;
import com.example.workoutmate.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatMessage> findChatRoomMessages(Long chatRoomId, Long cursor, int size) {

        QChatMessage chatMessage = QChatMessage.chatMessage;
        QUser sender = QUser.user;

        return queryFactory
                .selectFrom(chatMessage)
                .join(chatMessage.sender, sender).fetchJoin()
                .where(
                        chatMessage.chatRoomId.eq(chatRoomId),
                        cursor != null ? chatMessage.id.lt(cursor) : null
                )
                .orderBy(chatMessage.id.desc())
                .limit(size)
                .fetch();
    }
}
