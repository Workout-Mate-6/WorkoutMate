package com.example.workoutmate.domain.recommend.v3.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VectorMigrationService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final UserVectorService userVectorService;
    private final BoardVectorService boardVectorService;

    /**
     * 벡터가 없는 모든 유저에 대해 벡터 생성
     * @return 처리된 유저 수
     */
    @Transactional
    public int migrateUserVectors() {
        int processedCount = 0;
        int batchSize = 100;
        int page = 0;

        while (true) {
            PageRequest pageRequest = PageRequest.of(page, batchSize);
            Page<User> users = userRepository.findAll(pageRequest);

            if (users.isEmpty()) {
                break;
            }

            for (User user : users.getContent()) {
                // 이메일 인증된 유저만 처리
                if (user.isEmailVerified() && !user.isDeleted()) {
                    // 벡터가 없으면 생성
                    if (!userVectorService.hasUserVector(user.getId())) {
                        userVectorService.createInitialUserVector(user.getId());
                        processedCount++;
                    }
                }
            }

            if (!users.hasNext()) {
                break;
            }
            page++;
        }

        return processedCount;
    }

    /**
     * 벡터가 없는 모든 게시글에 대해 벡터 생성
     * @return 처리된 게시글 수
     */
    @Transactional
    public int migrateBoardVectors() {
        int processedCount = 0;
        int batchSize = 100;
        int page = 0;

        while (true) {
            PageRequest pageRequest = PageRequest.of(page, batchSize);
            Page<Board> boards = boardRepository.findAllByIsDeletedFalse(pageRequest);

            if (boards.isEmpty()) {
                break;
            }

            for (Board board : boards.getContent()) {
                // 벡터가 없으면 생성
                if (!boardVectorService.hasBoardVector(board.getId())) {
                    boardVectorService.createBoardVector(board);
                    processedCount++;
                }
            }

            if (!boards.hasNext()) {
                break;
            }
            page++;
        }

        return processedCount;
    }

    /**
     * 전체 마이그레이션 실행
     * @return 처리 결과
     */
    @Transactional
    public MigrationResult migrateAll() {
        int userCount = migrateUserVectors();
        int boardCount = migrateBoardVectors();

        return new MigrationResult(userCount, boardCount);
    }

    /**
     * 마이그레이션 결과 DTO
     */
    public record MigrationResult(int userVectorCount, int boardVectorCount) {
        public String getSummary() {
            return String.format("Migration completed: %d user vectors, %d board vectors created",
                    userVectorCount, boardVectorCount);
        }
    }
}
