package com.example.workoutmate.domain.recommend.v3.support;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.board.enums.Status;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import com.example.workoutmate.domain.follow.entity.Follow;
import com.example.workoutmate.domain.follow.repository.FollowRepository;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.participation.repository.ParticipationRepository;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.enums.UserGender;
import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.domain.zzim.entity.Zzim;
import com.example.workoutmate.domain.zzim.repository.ZzimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DemoDataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ParticipationRepository participationRepository;
    private final FollowRepository followRepository;
    private final ZzimRepository zzimRepository;
    private final PasswordEncoder passwordEncoder;

    // 시더 동작 스위치(원하면 yml로 뺄 수 있음)
    private static final boolean ENABLED = true;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!ENABLED) return;
        if (userRepository.count() > 0 || boardRepository.count() > 0) {
            log.info("DemoDataLoader: 데이터가 이미 존재하여 시딩을 건너뜁니다.");
            return;
        }
        log.info("DemoDataLoader: 시딩 시작");

        // 1) 유저 30명 생성 (이메일/암호화/성별)
        List<User> users = createUsers(30);

        // 2) 팔로우 관계(무작위 양방향 일부 생성)
        createFollows(users);

        // 3) 게시글 생성 (작성자는 아무 유저나, 시작시간=미래만, 최대 5명)
        List<Board> boards = createBoards(users, 25);

        // 4) 찜(zzim) 생성 (무작위)
        createZzims(users, boards, 60);

        // 5) 참여 생성 (게시글당 최대 5명, ACCEPTED까지 채움)
        createParticipations(users, boards);

        log.info("DemoDataLoader: 시딩 완료 - users={}, boards={}", users.size(), boards.size());
    }

    /* ---------- 유틸/시딩 로직 ---------- */

    private List<User> createUsers(int n) {
        List<User> out = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            String email = "user" + i + "@demo.com";
            String rawPw = "Passw0rd!" + i;
            User u = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPw))
                    .name("사용자" + i)
                    .gender(i % 2 == 0 ? UserGender.Male : UserGender.Female)
                    .role(UserRole.GUEST)
                    .isDeleted(false)
                    .isEmailVerified(true)
                    .build();
            out.add(u);
        }
        userRepository.saveAll(out);
        return out;
    }

    private void createFollows(List<User> users) {
        Random rnd = ThreadLocalRandom.current();
        int edges = Math.max(15, users.size()); // 대략 유저 수만큼
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < edges; i++) {
            User a = users.get(rnd.nextInt(users.size()));
            User b = users.get(rnd.nextInt(users.size()));
            if (a.getId().equals(b.getId())) continue;

            String keyAB = a.getId() + "_" + b.getId();
            String keyBA = b.getId() + "_" + a.getId();
            if (seen.contains(keyAB) || seen.contains(keyBA)) continue;

            Follow f = Follow.builder().follower(a).following(b).build();
            followRepository.save(f);

            // 절반 확률로 양방향
            if (rnd.nextBoolean()) {
                Follow f2 = Follow.builder().follower(b).following(a).build();
                followRepository.save(f2);
            }
            seen.add(keyAB);
            seen.add(keyBA);
        }
    }

    private List<Board> createBoards(List<User> users, int nBoards) {
        Random rnd = ThreadLocalRandom.current();
        SportType[] types = SportType.values();

        List<Board> boards = new ArrayList<>(nBoards);
        for (int i = 0; i < nBoards; i++) {
            User writer = users.get(rnd.nextInt(users.size()));

            // 시작 시간: 앞으로 1시간~20일 사이, 6시간 단위 스냅
            LocalDateTime start = randomFutureSlot(rnd);

            Long max = (long) (2 + rnd.nextInt(4)); // 2~5
            Board b = Board.builder()
                    .title(sampleTitle(types, rnd))
                    .content("더미 내용입니다. 함께 운동하실 분 구해요!")
                    .sportType(types[rnd.nextInt(types.length)])
                    .maxParticipants(max)
                    .currentParticipants(0L)
                    .startTime(start)
                    .status(Status.OPEN)
                    .writer(writer)
                    .build();
            boards.add(b);
        }
        boardRepository.saveAll(boards);
        return boards;
    }

    private LocalDateTime randomFutureSlot(Random rnd) {
        int addDays = 1 + rnd.nextInt(20);
        int slot = new int[]{6, 12, 18, 21}[rnd.nextInt(4)];
        return LocalDateTime.now()
                .plusDays(addDays)
                .with(LocalTime.of(slot, 0))
                .withSecond(0).withNano(0);
    }

    private String sampleTitle(SportType[] types, Random rnd) {
        SportType t = types[rnd.nextInt(types.length)];
        String[] adjectives = {"가볍게", "빡세게", "초보환영", "재밌게", "저녁팟", "아침팟"};
        return "[" + t.name() + "] " + adjectives[rnd.nextInt(adjectives.length)];
    }

    private void createZzims(List<User> users, List<Board> boards, int trials) {
        Random rnd = ThreadLocalRandom.current();
        Set<String> dup = new HashSet<>();
        for (int i = 0; i < trials; i++) {
            User u = users.get(rnd.nextInt(users.size()));
            Board b = boards.get(rnd.nextInt(boards.size()));
            if (Objects.equals(b.getWriter().getId(), u.getId())) continue;
            String key = u.getId() + "_" + b.getId();
            if (!dup.add(key)) continue;

            Zzim z = Zzim.builder().user(u).board(b).build();
            zzimRepository.save(z);
        }
    }

    private void createParticipations(List<User> users, List<Board> boards) {
        Random rnd = ThreadLocalRandom.current();

        for (Board b : boards) {
            Long cap = b.getMaxParticipants();
            int want = 1 + rnd.nextInt((int) Math.max(1, cap));
            List<User> shuffled = new ArrayList<>(users);
            Collections.shuffle(shuffled, rnd);

            int accepted = 0;
            for (User u : shuffled) {
                if (accepted >= want) break;
                if (Objects.equals(u.getId(), b.getWriter().getId())) continue;

                Participation p = Participation.builder()
                        .board(b)
                        .applicant(u)
                        .state(ParticipationState.ACCEPTED)
                        .build();
                participationRepository.save(p);
                accepted++;
            }

            // 새로 추가한 메서드 사용
            b.updateCurrentParticipants(accepted);
            boardRepository.save(b);
        }
    }
}

