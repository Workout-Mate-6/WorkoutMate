package com.example.workoutmate.domain.board.service;

import com.example.workoutmate.domain.board.dto.BoardFilterRequestDto;
import com.example.workoutmate.domain.board.dto.BoardRequestDto;
import com.example.workoutmate.domain.board.dto.BoardResponseDto;
import com.example.workoutmate.domain.board.dto.BoardSportTypeResponseDto;
import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.BoardMapper;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.board.repository.BoardQueryRepository;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import com.example.workoutmate.domain.follow.service.FollowService;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.service.UserService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;
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

    // 내 게시물 목록 조회
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getMyBoards(CustomUserPrincipal customUserPrincipal, Pageable pageable) {
        User user = userService.findById(customUserPrincipal.getId());

        Page<Board> boardPage = boardRepository.findAllByWriterAndIsDeletedFalse(user, pageable);

        return boardPage.map(BoardMapper::boardToBoardResponse);
    }

    //게시글 수정
    @Transactional
    public BoardResponseDto updateBoard(Long boardId, Long userId, BoardRequestDto requestDto) {

        Board board = boardSearchService.getBoardById(boardId);

        // 작성자 권한 체크
        validateBoardWriter(userId, board);

        // 모집인원 수정시, 이미 모집된 인원보다 항상 커야함
        if (requestDto.getMaxParticipants() < board.getCurrentParticipants())
            throw new CustomException(CustomErrorCode.INVALID_MAX_PARTICIPANTS);

        board.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getSportType(), requestDto.getMaxParticipants());

        // entity -> dto
        return BoardMapper.boardToBoardResponse(board);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {

        Board board = boardSearchService.getBoardById(boardId);

        validateBoardWriter(userId, board);

        // 현재 모집된 인원이 있을경우 삭제 불가
        if (board.getCurrentParticipants() > 0) {
            throw new CustomException(CustomErrorCode.BOARD_HAS_PARTICIPANTS);
        }

        board.delete();
    }

    // 작성자 권한 체크 메서드
    public void validateBoardWriter(Long userId, Board board) {
        if (!board.getWriter().getId().equals(userId)) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_BOARD_ACCESS);
        }
    }

    // participation쪽 동시성 락 구현때문에 메서드 제작..
    @Transactional(readOnly = true)
    public Board findByIdWithPessimisticLock(Long id) {
        return boardRepository.findByIdWithPessimisticLock(id).orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    }

    // 운동 종목 카테고리 항목 조회
    @Transactional(readOnly = true)
    public BoardSportTypeResponseDto getAllSportTypes() {

        List<String> sportTypes = Arrays.stream(SportType.values())
                .map(Enum::name) // "RUNNING", "FOOTBALL",...
                .collect(Collectors.toList());

        return new BoardSportTypeResponseDto(sportTypes);
    }

    // 게시글 통합 조회(필터링) 기능
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> searchBoards(Long userId, BoardFilterRequestDto filterRequestDto, Pageable pageable) {
        Page<Board> boardPage = boardQueryRepository.searchWithFilters(userId, filterRequestDto, pageable);

        return boardPage.map(BoardMapper::boardToBoardResponse);
    }

    // 내가 작성하지 않고 삭제되지 않은 게시글 찾기
    public List<Board> findRecommendationCandidates(Long userId, Pageable pageable) {
        return boardRepository.findAllByWriterIdNotAndIsDeletedFalse(userId, pageable);
    }
}
