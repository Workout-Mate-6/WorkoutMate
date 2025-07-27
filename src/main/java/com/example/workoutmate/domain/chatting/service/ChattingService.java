package com.example.workoutmate.domain.chatting.service;

import com.example.workoutmate.domain.chatting.dto.ChatMessageResponseDto;
import com.example.workoutmate.domain.chatting.dto.ChatRoomCreateResponseDto;
import com.example.workoutmate.domain.chatting.dto.ChatRoomResponseDto;
import com.example.workoutmate.domain.chatting.entity.ChatMessage;
import com.example.workoutmate.domain.chatting.entity.ChatRoom;
import com.example.workoutmate.domain.chatting.entity.ChatRoomMember;
import com.example.workoutmate.domain.chatting.entity.ChattingMapper;
import com.example.workoutmate.domain.chatting.repository.ChatMessageRepository;
import com.example.workoutmate.domain.chatting.repository.ChatRoomMemberRepository;
import com.example.workoutmate.domain.chatting.repository.ChatRoomRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.workoutmate.global.enums.CustomErrorCode.CHATROOM_NOT_FOUND;
import static com.example.workoutmate.global.enums.CustomErrorCode.EQUALS_SENDER_RECEIVER;

@Service
@RequiredArgsConstructor
public class ChattingService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatRoomCreateResponseDto createChatRoom(Long receiverId, CustomUserPrincipal authUser) {
        User sender = userService.findById(authUser.getId());
        User receiver = userService.findById(receiverId);

        if(Objects.equals(sender.getId(), receiver.getId())) {
            throw new CustomException(EQUALS_SENDER_RECEIVER, EQUALS_SENDER_RECEIVER.getMessage());
        }

        // 삭제되지않은 채팅방이 있는 경우
        Optional<ChatRoom> existingRoom = chatRoomRepository
                .findByUsersAndNotDeleted(sender.getId(), receiver.getId());

        // 기존 채팅방 반환
        if (existingRoom.isPresent()) {
            ChatRoom chatRoom = existingRoom.get();
            return new ChatRoomCreateResponseDto(
                    chatRoom.getId(), sender.getId(), receiver.getId(), chatRoom.getCreatedAt());
        }

        // 채팅방 (Chatroom) 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .build();
        chatRoomRepository.save(chatRoom);

        // chat_room_member에 sender 등록
        ChatRoomMember senderMember = ChatRoomMember.builder()
                .userId(sender.getId())
                .chatRoomId(chatRoom.getId())
                .joinedAt(chatRoom.getCreatedAt())
                .build();
        chatRoomMemberRepository.save(senderMember);

        // chat_room_member에 receiver 등록
        ChatRoomMember receiverMember = ChatRoomMember.builder()
                .userId(receiver.getId())
                .chatRoomId(chatRoom.getId())
                .joinedAt(chatRoom.getCreatedAt())
                .build();
        chatRoomMemberRepository.save(receiverMember);

        return ChattingMapper.toCreateDto(chatRoom);
    }


    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getMyChatRooms(CustomUserPrincipal authUser, LocalDateTime cursor, Integer size) {
        User user = userService.findById(authUser.getId());

        return chatRoomMemberRepository.findMyChatRooms(user.getId(), cursor, size);
    }


    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getChatRoomMessage(Long chatRoomId, CustomUserPrincipal authUser, Long cursor, Integer size) {
        User user = userService.findById(authUser.getId());

        ChatRoom chatRoom = chatRoomRepository.findByIdAndIsDeletedFalse(chatRoomId).orElseThrow(
                () -> new CustomException(CHATROOM_NOT_FOUND, CHATROOM_NOT_FOUND.getMessage()));

        if (!chatRoom.getSenderId().equals(user.getId()) &&
                !chatRoom.getReceiverId().equals(user.getId())) {
            throw new CustomException(CHATROOM_NOT_FOUND); // 채팅방 접근 권한 X
        }

        List<ChatMessage> chatMessageList = chatMessageRepository.findChatRoomMessages(chatRoomId, cursor, size);

        return chatMessageList.stream()
                .map(ChattingMapper::toMessageDto)
                .collect(Collectors.toList());
    }
}
