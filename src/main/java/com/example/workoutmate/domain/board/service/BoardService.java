package com.example.workoutmate.domain.board.service;

import com.example.workoutmate.domain.board.controller.dto.BoardRequestDto;
import com.example.workoutmate.domain.board.controller.dto.BoardResponseDto;
import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

//    // 게시글 생성/저장
//    @Transactional
//    public BoardResponseDto createBoard(Long writerId, BoardRequestDto requestDto) {
//
//        // 유저 조회 -> userService에서 조회기능 사용 UserService에서 서비스 코드 요청하기
//
//
//        Board board = Board.builder()
//                .user(user)
//                .title(requestDto.getTitle())
//                .content(requestDto.getContent())
//                .sportType(requestDto.getSportType())
//                .build();
//
//        boardRepository.save(board);
//
//        return new BoardResponseDto(board);
//    }


    // 게시글 단건 조회 메서드
    @Transactional(readOnly = true)
    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + boardId)); // 추후 globalException으로 수정 예정
    }
}
