package com.example.workoutmate.domain.board.service;

import com.example.workoutmate.domain.board.controller.dto.BoardRequestDto;
import com.example.workoutmate.domain.board.controller.dto.BoardResponseDto;
import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;

    // 게시글 생성/저장
    @Transactional
    public BoardResponseDto createBoard(Long writerId, BoardRequestDto requestDto) {

        // 유저 조회 -> userService에서 조회기능 사용 UserService에서 서비스 코드 요청하기
        User user = userService.findById(writerId);

        Board board = Board.builder()
                .writer(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .sportType(requestDto.getSportType())
                .build();

        boardRepository.save(board);

        return new BoardResponseDto(board);
    }



    // 다른 서비스에서 게시글 단건 조회 시 사용 서비스
    // 게시글 단건 조회 메서드
    @Transactional(readOnly = true)
    public Board getBoardById(Long boardId) {
        return boardRepository.findByIdAndIsDeletedFalse(boardId) // 삭제되지 않은 게시글(isDeleted = false)만 조회
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + boardId)); // 추후 globalException으로 수정 예정
    } // 사용법 예시) Board board = boardService.getBoardById(boardId);
}
