package com.example.workoutmate.domain.chatting.service;

import com.example.workoutmate.domain.chatting.dto.ChatDto;
import com.example.workoutmate.domain.chatting.entity.ChatMessage;
import com.example.workoutmate.domain.chatting.entity.ChattingMapper;
import com.example.workoutmate.domain.chatting.event.ChatPublisher;
import com.example.workoutmate.domain.chatting.repository.ChatMessageRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final ChatPublisher chatPublisher;

    @Transactional
    public void save(ChatDto chatDto, String email) {
        User sender = userService.findByEmail(email);

        chatDto.setTypeTalk();
        ChatMessage chatMessage = ChattingMapper.toChatMessage(chatDto, sender);

        chatMessageRepository.save(chatMessage);

        ChatDto chat = ChattingMapper.toChatDto(chatMessage);

        chatPublisher.sendMessage(chat.getChatRoomId(), chat);
    }
}
