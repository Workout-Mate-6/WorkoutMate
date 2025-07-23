package com.example.workoutmate.domain.board.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final BoardRepository boardRepository;

    // 게시글 단건 조회 메서드
    @Transactional(readOnly = true)
    public Board getBoardById(Long boardId) {
        return boardRepository.findByIdAndIsDeletedFalse(boardId) // 삭제되지 않은 게시글(isDeleted = false)만 조회
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    } // 사용법 예시) Board board = boardSearchService.getBoardById(boardId);

//    // 사용자의 게시글 총 갯수 조회 메서드(삭제되지 않은 게시글만)
//    @Transactional(readOnly = true)
//    public int countBoardsByWriter(Long writerId) {
//        return boardRepository.countByWriter_IdAndIsDeletedFalse(writerId);
//    }
}
