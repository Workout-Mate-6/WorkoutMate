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

import static com.example.workoutmate.global.enums.CustomErrorCode.*;

/**
 * 채팅 관련 기능 클래스
 * 채팅방 생성, 내 채팅방 조회, 채팅방 메시지 조회, 채팅방 나가기
 *
 * @author 이현하
 */
@Service
@RequiredArgsConstructor
public class ChattingService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;


    /**
     * 채팅방 생성
     *
     * @param receiverId 채팅할 상대 id
     * @param authUser 로그인한 유저 정보
     * @return 채팅방 생성 정보
     */
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

            ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByChatRoomIdAndUserId(chatRoom.getId(), sender.getId()).orElseThrow(
                    () -> new CustomException(CHATROOM_MEMBER_NOT_FOUND, CHATROOM_MEMBER_NOT_FOUND.getMessage()));

            chatRoomMember.join();

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


    /**
     * 내 채팅방 목록 조회
     *
     * @param authUser 로그인한 유저 정보
     * @param cursor 이전 페이지의 마지막 채팅방 메시지 시간
     * @param size 한 페이지에 조회할 채팅방 개수
     * @return 조회된 내 채팅방 정보
     */
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getMyChatRooms(CustomUserPrincipal authUser, LocalDateTime cursor, Integer size) {
        User user = userService.findById(authUser.getId());

        return chatRoomMemberRepository.findMyChatRooms(user.getId(), cursor, size);
    }


    /**
     * 채팅방 메시지 조회
     *
     * @param chatRoomId 조회할 채팅방 id
     * @param authUser 로그인한 유저 정보
     * @param cursor 이전 페이지의 마지막 메시지 id
     * @param size 한 페이지에 조회할 메시지 개수
     * @return 조회된 채팅방 메시지 정보
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getChatRoomMessage(Long chatRoomId, CustomUserPrincipal authUser, Long cursor, Integer size) {
        User user = userService.findById(authUser.getId());

        ChatRoom chatRoom = findChatRoomById(chatRoomId);

        validateUserInChatRoom(chatRoom, user);

        List<ChatMessage> chatMessageList = chatMessageRepository.findChatRoomMessages(chatRoomId, cursor, size);

        return chatMessageList.stream()
                .map(ChattingMapper::toMessageDto)
                .collect(Collectors.toList());
    }


    /**
     * 채팅방 나가기
     *
     * @param chatRoomId 채팅방 id
     * @param authUser 로그인한 유저 정보
     */
    @Transactional
    public void leaveChatRoom(Long chatRoomId, CustomUserPrincipal authUser) {
        User user = userService.findById(authUser.getId());

        ChatRoom chatRoom = findChatRoomById(chatRoomId);

        validateUserInChatRoom(chatRoom, user);

        ChatRoomMember member = chatRoomMemberRepository.findByUserIdAndChatRoomId(user.getId(), chatRoomId)
                .orElseThrow(() -> new CustomException(CHATROOM_MEMBER_NOT_FOUND, "채팅방 멤버가 존재하지 않습니다."));

        // 채팅방 퇴장
        member.leave();

        // 채팅방 멤버 모두 퇴장 상태면 채팅방 soft delete
        boolean allLeft = chatRoomMemberRepository.countByChatRoomIdAndIsJoinedTrue(chatRoomId) == 0;

        if (allLeft && !chatRoom.isDeleted()) {
            chatRoom.delete();
        }
    }



    /* 메서드 분리 */

    // 채팅방에 멤버가 있는지 확인
    private void validateUserInChatRoom(ChatRoom chatRoom, User user) {
        if (!chatRoom.getSenderId().equals(user.getId()) && !chatRoom.getReceiverId().equals(user.getId())) {
            throw new CustomException(CHATROOM_MEMBER_NOT_FOUND, CHATROOM_MEMBER_NOT_FOUND.getMessage());
        }
    }

    // id로 ChatRoom 찾기
    private ChatRoom findChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(CHATROOM_NOT_FOUND, CHATROOM_NOT_FOUND.getMessage()));
    }
}
