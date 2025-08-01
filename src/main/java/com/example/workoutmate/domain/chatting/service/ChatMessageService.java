package com.example.workoutmate.domain.chatting.service;

import com.example.workoutmate.domain.chatting.dto.ChatDto;
import com.example.workoutmate.domain.chatting.entity.ChatMessage;
import com.example.workoutmate.domain.chatting.entity.ChattingMapper;
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

    @Transactional
    public ChatDto save(ChatDto chatDto) {
        User sender = userService.findById(chatDto.getSenderId());

        ChatMessage chatMessage = ChattingMapper.toChatMessage(chatDto, sender);

        chatMessageRepository.save(chatMessage);

        return ChattingMapper.toChatDto(chatMessage);
    }
}
