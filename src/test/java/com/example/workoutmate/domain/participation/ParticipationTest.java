package com.example.workoutmate.domain.participation;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import com.example.workoutmate.domain.comment.entity.Comment;
import com.example.workoutmate.domain.comment.repository.CommentRepository;
import com.example.workoutmate.domain.participation.dto.ParticipationRequestDto;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.participation.repository.ParticipationRepository;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.enums.UserGender;
import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.connection.isolation=2", // READ_COMMITTED
        "spring.datasource.hikari.maximum-pool-size=20"
})
public class ParticipationTest {

    @Autowired
    private ParticipationService participationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ParticipationRepository participationRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Board board;
    private List<User> testUsers;

    private static final int THREAD_COUNT = 10;
    private static final Long TARGET_COUNT = 3L;

    // 데이터 사전 준비 준비 || 불필요한 DB 경합 방지를 위해
    @BeforeEach
    @Transactional
    void setUp() {
        // 기존 데이터 정리
        participationRepository.deleteAll();
        commentRepository.deleteAll();
        boardRepository.deleteAll();
        userRepository.deleteAll();

        // 작성자 생성
        User writer = createUser("writer@gmail.com");
        userRepository.saveAndFlush(writer);

        // 게시글 생성
        board = createBoard(writer);
        boardRepository.saveAndFlush(board);

        // 테스트용 사용자들 미리 생성
        testUsers = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            User user = createUser("user" + i + "@email.com");
            userRepository.saveAndFlush(user);
            testUsers.add(user);
        }
    }

    @Test
    void 동시성_테스트_모집인원이_초과되면_안된다() throws Exception {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT); // ExecutorService 자바에서 쓰레드를 효율적으로 관리할 수 있게 도와주는 기능
        AtomicInteger successCount = new AtomicInteger(0); // AtomicInteger : 멀티스레드 환경에서 값을안전하게 증가시키는 변수
        AtomicInteger failCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // when - CompletableFuture 사용으로 더 안정적인 동시성 테스트
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;

            // 멀티 스레딩 환경에서 비동기 작업 실행
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // 비동기 작업 시작
                try {
                    latch.await(); // latch가 열릴 때까지 대기
                    User applicant = testUsers.get(index);

                    // 각 스레드에서 별도의 트랜잭션으로 댓글과 참여 정보 생성
                    Comment comment = createComment(applicant, board);
                    commentRepository.saveAndFlush(comment); // JPA 메서드로 즉시 DB에 반영
                    // 댓글:요청 -> 게시글:수락 -> 댓글:참여
                    // 수락 상태인걸로 만들기
                    Participation participation = createParticipation(applicant, board, comment, ParticipationState.ACCEPTED); // 충돌 유발을 하기 위해 참여자 정보를 수락 상태로 미리 저장
                    participationRepository.saveAndFlush(participation); // JPA 메서드로 즉시 DB에 반영

                    ParticipationRequestDto dto = new ParticipationRequestDto("참여");
                    CustomUserPrincipal authUser = new CustomUserPrincipal(
                            applicant.getId(),
                            applicant.getEmail(),
                            applicant.getRole()
                    );

                    // 실제 참여 처리 (여기서 동시성 제어가 일어남)
                    participationService.cancelParticipation(board.getId(), dto, authUser);
                    successCount.incrementAndGet();

                } catch (CustomException e) {
                    if (e.getErrorCode() == CustomErrorCode.BOARD_FULL) { // 커스텀 예외는 에러 메세지만 출력
                        failCount.incrementAndGet();
                        System.out.println("Thread " + index + ": 인원 초과로 참여 실패");
                    } else {
                        System.err.println("Thread " + index + ": CustomException - " + e.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("Thread " + index + ": 예상치 못한 예외 - " + e.getMessage());
                    e.printStackTrace();
                }
            }, executor);

            futures.add(future); // 모든 비동기 작업이 완료될때까지 기다리기 위해
        }
        latch.countDown();
        // 모든 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join(); // 모든 비동기 작업이 끝날때까지 현재 쓰레드 멈추고 기다리기
        executor.shutdown(); // 이후에 스레드들은 소멸

        // then
        Board result = boardRepository.findById(board.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));

        System.out.println("=== 테스트 결과 ===");
        System.out.println("성공한 참여자 수: " + successCount.get());
        System.out.println("실패한 참여자 수: " + failCount.get());
        System.out.println("최종 현재 인원: " + result.getCurrentCount());
        System.out.println("목표 인원: " + result.getTargetCount());
        System.out.println("전체 시도 횟수: " + (successCount.get() + failCount.get()));

        // 검증: 현재 인원이 목표 인원을 초과하지 않는지...
        assertThat(result.getCurrentCount())
                .as("현재 인원이 목표 인원을 초과하면 안됨")
                .isLessThanOrEqualTo(result.getTargetCount());

        // 성공한 참여자 수는 목표 인원과 같거나 작아야됨
        assertThat(successCount.get())
                .as("성공한 참여자 수는 목표 인원을 초과할 수 없음")
                .isLessThanOrEqualTo(TARGET_COUNT.intValue());

        // 동시성 제어가 제대로 작동했다면, 일부는 성공하고 일부는 실패해야 함
        if (THREAD_COUNT > TARGET_COUNT) {
            assertThat(failCount.get())
                    .as("목표 인원보다 많은 스레드가 실행되면 일부는 실패해야 함")
                    .isGreaterThan(0);
        }

        // 전체 처리 완료 검증
        assertThat(successCount.get() + failCount.get())
                .as("모든 스레드가 처리되어야 함")
                .isEqualTo(THREAD_COUNT);
    }

    private Participation createParticipation(User user, Board board, Comment comment, ParticipationState participationState) {
        return Participation.builder()
                .applicant(user)
                .board(board)
                .comment(comment)
                .state(participationState)
                .build();
    }

    private Board createBoard(User writer) {
        return Board.builder()
                .writer(writer)
                .title("동시성 테스트 게시글")
                .content("인원 제한 테스트용 게시글입니다.")
                .sportType(SportType.FOOTBALL)
                .targetCount(TARGET_COUNT)
                .currentCount(0L)
                .build();
    }

    private Comment createComment(User user, Board board) {
        return Comment.builder()
                .content("참여할게요! - " + user.getEmail())
                .writer(user)
                .board(board)
                .build();
    }

    private User createUser(String email) {
        return User.builder()
                .email(email)
                .password("Test1234@")
                .name("TestUser_" + email.split("@")[0])
                .gender(UserGender.Male)
                .role(UserRole.GUEST)
                .build();
    }
}