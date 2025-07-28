package com.example.workoutmate.domain.zzim.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.service.BoardSearchService;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.domain.zzim.controller.dto.ZzimResponseDto;
import com.example.workoutmate.domain.zzim.entity.Zzim;
import com.example.workoutmate.domain.zzim.repository.ZzimRepository;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ZzimService {

    private final ZzimRepository zzimRepository;
    private final UserService userService;
    private final BoardSearchService boardSearchService;

    @Transactional
    public ZzimResponseDto createZzim(Long boardId, Long userId) {

        Board board = boardSearchService.getBoardById(boardId);

        User user = userService.findById(userId);

        // 본인 게시글 찜 방지
        if (board.getWriter().getId().equals(userId)) {
            throw new CustomException(CustomErrorCode.CANNOT_ZZIM_OWN_BOARD);
        }

        // 중복 찜 방지
        zzimRepository.findByBoardAndUser(board, user)
                .ifPresent(existing -> {
                    throw new CustomException(CustomErrorCode.ALREADY_ZZIM);
                });

        Zzim zzim = Zzim.of(board, user);
        Zzim savedZzim = zzimRepository.save(zzim);

        return new ZzimResponseDto(savedZzim);
    }
}
