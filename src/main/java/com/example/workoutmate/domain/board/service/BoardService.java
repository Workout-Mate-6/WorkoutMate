package com.example.workoutmate.domain.board.service;

import com.example.workoutmate.domain.board.controller.dto.BoardRequestDto;
import com.example.workoutmate.domain.board.controller.dto.BoardResponseDto;
import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import com.example.workoutmate.domain.follow.service.FollowService;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;
    private final FollowService followService;

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

    // 게시글 단건 조회
    @Transactional
    public BoardResponseDto getBoard(Long boardId) {
        Board board = getBoardById(boardId); // 게시글 단건 조회 메서드

        return new BoardResponseDto(board);
    }

    // 게시글 전체 조회
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getAllBoards(Pageable pageable) {

        return boardRepository.findAllByIsDeletedFalse(pageable)
                .map(BoardResponseDto::new); // Page<Board> → Page<BoardResponseDto>
    }

    // 팔로잉한 유저 게시글 전체 조회
    public Page<BoardResponseDto> getBoardsFromFollowings(Long myUserId, Pageable pageable) {
        List<Long> followingIds = followService.getFollowingUserIds(myUserId);

        if (followingIds.isEmpty()) {
            return Page.empty(); // 빈페이지 반환
        }

        Page<Board> boardPage = boardRepository.findByWriter_IdIn(followingIds, pageable);

        return boardPage.map(BoardResponseDto::new);
    }

    // 운동 종목 별 카테고리 조회
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getBoardsByCategory(Pageable pageable, SportType sportType) {

        return boardRepository.findAllByIsDeletedFalseAndSportType(pageable, sportType)
                .map(BoardResponseDto::new);
    }

    //게시글 수정
    @Transactional
    public BoardResponseDto updateBoard(Long boardId, Long userId, BoardRequestDto boardRequestDto) {

        Board board = getBoardById(boardId);

        // 작성자 권한 체크
        if (!board.getWriter().getId().equals(userId)) {
            throw new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다.");
        }

        // Board엔티티 내부 update 메서드 호출
        board.update(boardRequestDto.getTitle(), boardRequestDto.getContent(), boardRequestDto.getSportType());

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
