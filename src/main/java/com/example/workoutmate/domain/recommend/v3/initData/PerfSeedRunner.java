package com.example.workoutmate.domain.recommend.v3.initData;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.board.repository.BoardRepository;
import com.example.workoutmate.domain.follow.entity.Follow;
import com.example.workoutmate.domain.follow.repository.FollowRepository;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.participation.repository.ParticipationRepository;
import com.example.workoutmate.domain.recommend.v3.service.BoardVectorService;
import com.example.workoutmate.domain.recommend.v3.service.UserVectorService;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.enums.UserGender;
import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.domain.zzim.entity.Zzim;
import com.example.workoutmate.domain.zzim.repository.ZzimRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Profile("dev")
@Component
@RequiredArgsConstructor
@Slf4j
class PerfSeedRunner implements ApplicationRunner {

    private final UserRepository userRepo;
    private final BoardRepository boardRepo;
    private final ParticipationRepository participationRepo;
    private final FollowRepository followRepo;
    private final ZzimRepository zzimRepo;

    private final BoardVectorService boardVectorService;
    private final UserVectorService userVectorService;

    private final SeedProperties seedProps;

    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager em;

    private final Random rnd = new Random(42);

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedProps.isEnabled()) {
            log.info("[SEED] disabled");
            return;
        }
        long t0 = System.currentTimeMillis();

        // 1) Users
        List<User> users = genUsers(seedProps.getUsers());
        batchSave(users, seedProps.getBatchSize());
        log.info("[SEED] users={} saved", users.size());

        // 2) Follows
        List<Follow> follows = genFollows(users, seedProps.getFollowsPerUser());
        batchSave(follows, seedProps.getBatchSize());
        log.info("[SEED] follows={} saved", follows.size());

        // 3) Boards
        List<Board> boards = genBoards(users,
                seedProps.getBoards(),
                seedProps.getMaxParticipantsMin(), seedProps.getMaxParticipantsMax(),
                seedProps.getNearFullRatio(), seedProps.getFullRatio());
        batchSave(boards, seedProps.getBatchSize());
        log.info("[SEED] boards={} saved", boards.size());

        // 4) Participation (ACCEPTED) — 각 보드의 currentParticipants 수만큼 생성
        List<Participation> parts = genParticipations(boards, users);
        batchSave(parts, seedProps.getBatchSize());
        log.info("[SEED] participations={} saved", parts.size());

        // 5) Zzim — 유저당 확률로 0~2개
        List<Zzim> zzims = genZzims(users, boards, seedProps.getZzimRatio());
        batchSave(zzims, seedProps.getBatchSize());
        log.info("[SEED] zzims={} saved", zzims.size());

        // 6) Vector upsert — 게시글 전량, 유저 상위 N명(옵션)
        for (Board b : boards) boardVectorService.upsert(b);
        users.stream().limit(Math.min(users.size(), 5000))
                .forEach(u -> userVectorService.upsert(u.getId(), userVectorService.buildFromBehavior(u.getId())));

        long ms = System.currentTimeMillis() - t0;
        log.info("[SEED] done in {} ms", ms);
    }

    // ---------- generators ----------

    private List<User> genUsers(int n) {
        List<User> out = new ArrayList<>(n + 2);
        String encPw = passwordEncoder.encode("pw");

        // 고정 테스트 계정 2개 (로그인/이메일인증 시나리오 점검용)
        User tester = new User("tester@test.com", encPw, "Tester",
                rnd.nextBoolean()?UserGender.Male:UserGender.Female, UserRole.GUEST);
        setField(tester, "isEmailVerified", true);
        out.add(tester);

        User tester2 = new User("unverified@test.com", encPw, "Tester2",
                rnd.nextBoolean()?UserGender.Male:UserGender.Female, UserRole.GUEST);
        setField(tester2, "isEmailVerified", false);
        setField(tester2, "verificationCode", "123456");
        setField(tester2, "verificationCodeExpiresAt", LocalDateTime.now().plusHours(1));
        out.add(tester2);

        // 일반 유저 n명
        for (int i = 0; i < n; i++) {
            String email = "user" + i + "@test.com";
            String name  = "U" + i;
            UserGender gender = (rnd.nextBoolean() ? UserGender.Male : UserGender.Female);
            UserRole role = UserRole.GUEST;

            User u = new User(email, encPw, name, gender, role);

            // 80%는 이메일 인증 완료, 20%는 미인증(+코드/만료)
            boolean verified = rnd.nextDouble() < 0.8;
            setField(u, "isEmailVerified", verified);
            if (!verified) {
                setField(u, "verificationCode", sixDigits());
                setField(u, "verificationCodeExpiresAt", LocalDateTime.now().plusHours(2));
            }
            out.add(u);
        }
        return out;
    }

    private List<Follow> genFollows(List<User> users, int followsPerUser) {
        int n = users.size();
        List<Follow> out = new ArrayList<>(n * Math.max(1, followsPerUser / 2));
        for (int i = 0; i < n; i++) {
            User u = users.get(i);
            int k = Math.max(0, (int) Math.round(rnd.nextGaussian() * (followsPerUser / 4.0) + followsPerUser));
            Set<Integer> picked = new HashSet<>();
            for (int j = 0; j < k; j++) {
                int idx;
                do { idx = rnd.nextInt(n); } while (idx == i || !picked.add(idx));
                out.add(new Follow(u, users.get(idx)));
            }
        }
        return out;
    }

    private List<Board> genBoards(List<User> users, int numBoards,
                                  int maxMin, int maxMax,
                                  double nearFullRatio, double fullRatio) {
        List<Board> out = new ArrayList<>(numBoards);
        SportType[] types = SportType.values();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < numBoards; i++) {
            User writer = users.get(rnd.nextInt(users.size()));
            SportType type = types[rnd.nextInt(types.length)];
            long max = rnd.nextInt(maxMax - maxMin + 1) + maxMin;

            Board b = Board.builder()
                    .writer(writer)
                    .title(type + " #" + i)
                    .content("auto seed content " + i)
                    .sportType(type)
                    .maxParticipants(max)
                    .build();

            // startTime: 임박/보통/원거리 섞기
            LocalDateTime st;
            int bucket = rnd.nextInt(100);
            if (bucket < 20) st = now.plusHours(rnd.nextInt(36));          // 임박(0~36h)
            else if (bucket < 70) st = now.plusDays(2 + rnd.nextInt(4));   // 보통(2~5d)
            else st = now.plusDays(6 + rnd.nextInt(10));                   // 원거리(6~15d)
            setField(b, "startTime", st);

            // 현원 설정(near-full/full 비율 반영)
            long cur;
            double r = rnd.nextDouble();
            if (r < fullRatio) cur = max; // FULL(필터 대상)
            else if (r < fullRatio + nearFullRatio) {
                long base = Math.max(0, (long) Math.floor(max * 0.9));
                cur = base + rnd.nextInt((int) Math.max(1, max - base));
            } else cur = rnd.nextInt((int) Math.max(1, max / 2));
            setField(b, "currentParticipants", cur);

            out.add(b);
        }
        return out;
    }

    private List<Participation> genParticipations(List<Board> boards, List<User> users) {
        List<Participation> out = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        int n = users.size();

        for (Board b : boards) {
            long cur = Math.min(b.getCurrentParticipants(), b.getMaxParticipants());
            if (cur <= 0) continue;

            Set<Integer> picked = new HashSet<>();
            for (int j = 0; j < cur; j++) {
                int idx;
                do { idx = rnd.nextInt(n); } while (!picked.add(idx));
                User u = users.get(idx);
                Participation p = Participation.builder()
                        .board(b)
                        .applicant(u)
                        .state(ParticipationState.ACCEPTED)
                        .build();
                // 최근/과거 섞기(친구 탐색 14일 트리거 포함)
                LocalDateTime created = (rnd.nextBoolean())
                        ? now.minusDays(rnd.nextInt(10))       // 최근 0~9일
                        : now.minusDays(10 + rnd.nextInt(40)); // 과거 10~49일
                setField(p, "createdAt", created); // BaseEntity.createdAt 가정
                out.add(p);
            }
        }
        return out;
    }

    private List<Zzim> genZzims(List<User> users, List<Board> boards, double prob) {
        List<Zzim> out = new ArrayList<>();
        int nB = boards.size();
        for (User u : users) {
            for (int i = 0; i < 2; i++) {
                if (rnd.nextDouble() < prob) out.add(Zzim.of(boards.get(rnd.nextInt(nB)), u));
            }
        }
        return out;
    }

    // ---------- 저장 유틸 ----------
    private <T> void batchSave(List<T> list, int batch) {
        int i = 0;
        for (int from = 0; from < list.size(); from += batch) {
            int to = Math.min(from + batch, list.size());
            List<T> sub = list.subList(from, to);
            // 타입에 따라 분기 저장
            Object first = sub.get(0);
            if (first instanceof User)      userRepo.saveAll((List<User>) sub);
            else if (first instanceof Board) boardRepo.saveAll((List<Board>) sub);
            else if (first instanceof Participation) participationRepo.saveAll((List<Participation>) sub);
            else if (first instanceof Follow) followRepo.saveAll((List<Follow>) sub);
            else if (first instanceof Zzim)   zzimRepo.saveAll((List<Zzim>) sub);
            em.flush();
            em.clear();
            i += sub.size();
        }
    }

    // 리플렉션 보조: setter 없는 필드 세팅
    private static void setField(Object target, String field, Object value) {
        Class<?> c = target.getClass();
        while (c != null) {
            try {
                java.lang.reflect.Field f = c.getDeclaredField(field);
                f.setAccessible(true);
                f.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass(); // 부모로 올라가서 계속 탐색
            } catch (Exception e) {
                throw new IllegalStateException("setField failed: " + field, e);
            }
        }
        throw new IllegalStateException("No such field in hierarchy: " + field);
    }

    private String sixDigits() {
        return String.format("%06d", rnd.nextInt(1_000_000));
    }
}
