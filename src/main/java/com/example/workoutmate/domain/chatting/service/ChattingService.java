package com.example.workoutmate.domain.chatting.service;

import com.example.workoutmate.domain.chatting.dto.ChatroomCreateResponseDto;
import com.example.workoutmate.domain.chatting.entity.Chatroom;
import com.example.workoutmate.domain.chatting.entity.ChatroomMember;
import com.example.workoutmate.domain.chatting.entity.ChattingMapper;
import com.example.workoutmate.domain.chatting.repository.ChatroomMemberRepository;
import com.example.workoutmate.domain.chatting.repository.ChatroomRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static com.example.workoutmate.global.enums.CustomErrorCode.EQUALS_SENDER_RECEIVER;

@Service
@RequiredArgsConstructor
public class ChattingService {

    private final ChatroomRepository chatroomRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final UserService userService;

    @Transactional
    public ChatroomCreateResponseDto createChatRoom(Long receiverId, CustomUserPrincipal authUser) {
        User sender = userService.findById(authUser.getId());
        User receiver = userService.findById(receiverId);

        if(Objects.equals(sender.getId(), receiver.getId())) {
            throw new CustomException(EQUALS_SENDER_RECEIVER, EQUALS_SENDER_RECEIVER.getMessage());
        }

        // 삭제되지않은 채팅방이 있는 경우
        Optional<Chatroom> existingRoom = chatroomRepository
                .findByUsersAndNotDeleted(sender.getId(), receiver.getId());

        // 기존 채팅방 반환
        if (existingRoom.isPresent()) {
            Chatroom chatroom = existingRoom.get();
            return new ChatroomCreateResponseDto(
                    chatroom.getId(), sender.getId(), receiver.getId(), chatroom.getCreatedAt());
        }

        // 채팅방 (Chatroom) 생성
        Chatroom chatroom = Chatroom.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .build();
        chatroomRepository.save(chatroom);

        // Chatroom_member에 저장
        ChatroomMember chatroomMember = ChatroomMember.builder()
                .userId(sender.getId())
                .chatroomId(chatroom.getId())
                .joinedAt(chatroom.getCreatedAt())
                .build();
        chatroomMemberRepository.save(chatroomMember);

        return ChattingMapper.toCreateDto(chatroom);
    }
}
