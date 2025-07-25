package com.example.workoutmate.domain.board.service;

import com.example.workoutmate.domain.board.dto.BoardRequestDto;
import com.example.workoutmate.domain.board.dto.BoardResponseDto;
import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.BoardMapper;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import com.example.workoutmate.domain.follow.service.FollowService;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardSearchService boardSearchService;
    private final UserService userService;
    private final FollowService followService;

    // 게시글 생성/저장
    @Transactional
    public BoardResponseDto createBoard(Long writerId, BoardRequestDto requestDto) {

        // 유저 조회 -> userService 에서 조회기능 사용
        User user = userService.findById(writerId);

        // dto -> entity
        Board board = BoardMapper.boardRequestToBoard(requestDto, user);

        boardRepository.save(board);

        // entity -> dto
        return BoardMapper.boardToBoardResponse(board);
    }

    // 게시글 단건 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(Long boardId) {

        Board board = boardSearchService.getBoardById(boardId);

        // entity -> dto
        return BoardMapper.boardToBoardResponse(board);
    }

    // 게시글 전체 조회
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getAllBoards(Pageable pageable) {

        Page<Board> boardPage = boardRepository.findAllByIsDeletedFalse(pageable);

        // Page<Board> → Page<BoardResponseDto>
        return boardPage.map(BoardMapper::boardToBoardResponse);
    }

    // 팔로잉한 유저 게시글 전체 조회
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getBoardsFromFollowings(Long myUserId, Pageable pageable) {
        List<Long> followingIds = followService.getFollowingUserIds(myUserId);

        if (followingIds.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0); // 빈페이지 반환
        }

        Page<Board> boardPage = boardRepository.findAllByWriterIdInWithWriterFetch(followingIds, pageable);

        // Page<Board> → Page<BoardResponseDto>
        return boardPage.map(BoardMapper::boardToBoardResponse);
    }

    // 운동 종목 별 카테고리 조회
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getBoardsByCategory(SportType sportType, Pageable pageable) {

        Page<Board> boardPage = boardRepository.findAllByIsDeletedFalseAndSportType(pageable, sportType);

        // Page<Board> → Page<BoardResponseDto>
        return boardPage.map(BoardMapper::boardToBoardResponse);
    }

    //게시글 수정
    @Transactional
    public BoardResponseDto updateBoard(Long boardId, Long userId, BoardRequestDto requestDto) {

        Board board = boardSearchService.getBoardById(boardId);

        // 작성자 권한 체크
        validateBoardWriter(userId,board);

        // 모집인원 수정시, 이미 모집된 인원보다 항상 커야함
        if(requestDto.getTargetCount()<board.getCurrentCount())
            throw new CustomException(CustomErrorCode.INVALID_TARGETCOUNT);

        board.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getSportType(), requestDto.getTargetCount());

        // entity -> dto
        return BoardMapper.boardToBoardResponse(board);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {

        Board board = boardSearchService.getBoardById(boardId);

        validateBoardWriter(userId, board);

        board.delete();
    }

    // 작성자 권한 체크 메서드
    public static void validateBoardWriter(Long userId, Board board) {
        if (!board.getWriter().getId().equals(userId)) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_BOARD_ACCESS);
        }
    }

}
