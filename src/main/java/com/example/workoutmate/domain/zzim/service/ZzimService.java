package com.example.workoutmate.domain.zzim.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.service.BoardSearchService;
import com.example.workoutmate.domain.notification.enums.NotificationType;
import com.example.workoutmate.domain.notification.service.NotificationService;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.domain.zzim.controller.dto.ZzimCountResponseDto;
import com.example.workoutmate.domain.zzim.controller.dto.ZzimResponseDto;
import com.example.workoutmate.domain.zzim.controller.dto.ZzimStatusResponseDto;
import com.example.workoutmate.domain.zzim.entity.Zzim;
import com.example.workoutmate.domain.zzim.repository.ZzimRepository;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZzimService {

    private final ZzimRepository zzimRepository;
    private final UserService userService;
    private final BoardSearchService boardSearchService;
    private final NotificationService notificationService;

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

        // 게시글 작성자에게 알림 전송
        notificationService.sendNotification(
                board.getWriter(), // 알림 받는 사람
                NotificationType.ZZIM, // 알림 타입
                String.format("%s님이 '%s' 게시글을 찜했습니다.", user.getName(), board.getTitle())
        );

        return new ZzimResponseDto(savedZzim);
    }

    @Transactional(readOnly = true)
    public Page<ZzimResponseDto> getZzimsByBoardId(Long boardId, Pageable pageable) {

        // 게시글 객체 조회
        Board board = boardSearchService.getBoardById(boardId);

        Page<Zzim> zzimPage = zzimRepository.findAllByBoard(board, pageable);

        return zzimPage.map(ZzimResponseDto::new);
    }

    @Transactional(readOnly = true)
    public ZzimCountResponseDto getZzimCountByBoardId(Long boardId) {

        // 게시글 조회
        Board board = boardSearchService.getBoardById(boardId);

        Long count = zzimRepository.countByBoard(board);

        return new ZzimCountResponseDto(boardId, count);
    }

    @Transactional(readOnly = true)
    public Page<ZzimResponseDto> getUserZzims(Long userId, Pageable pageable) {

        Page<Zzim> zzimPage = zzimRepository.findAllByUserId(userId, pageable);

        return zzimPage.map(ZzimResponseDto::new);
    }

    @Transactional
    public ZzimStatusResponseDto checkZzimStatus(Long boardId, Long userId) {

        Optional<Zzim> zzimOptional = zzimRepository.findByBoardIdAndUserId(boardId, userId);

        if (zzimOptional.isPresent()) {
            Zzim zzim = zzimOptional.get();

            return ZzimStatusResponseDto.builder()
                    .boardId(boardId)
                    .userId(userId)
                    .zzimmed(true)
                    .zzimId(zzim.getId())
                    .build();
        } else {

            return ZzimStatusResponseDto.builder()
                    .boardId(boardId)
                    .userId(userId)
                    .zzimmed(false)
                    .zzimId(null)
                    .build();
        }
    }

    @Transactional
    public void deleteZzim(Long zzimId, Long userId) {

        Zzim zzim = zzimRepository.findById(zzimId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.ZZIM_NOT_FOUND));

        if (!zzim.getUser().getId().equals(userId)) {
            throw  new CustomException(CustomErrorCode.FORBIDDEN_ZZIM_ACCESS);
        }

        zzimRepository.delete(zzim);
    }

    public Set<Long> getZzimBoardIdByUser(User user) {
        List<Zzim> zzims = zzimRepository.findAllByUser(user) ;
        return zzims.stream().map(z->z.getBoard().getId()).collect(Collectors.toSet());
    }

    public List<Zzim> findByUserId(Long userId) {
        return zzimRepository.findAllByUserId(userId);
    }
}
