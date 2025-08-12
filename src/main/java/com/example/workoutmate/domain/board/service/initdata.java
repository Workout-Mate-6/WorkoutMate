//package com.example.workoutmate.domain.board.service;
//
//import com.example.workoutmate.domain.board.entity.Board;
//import com.example.workoutmate.domain.board.entity.SportType;
//import com.example.workoutmate.domain.board.enums.Status;
//import com.example.workoutmate.domain.board.repository.BoardRepository;
//import com.example.workoutmate.domain.comment.entity.Comment;
//import com.example.workoutmate.domain.comment.repository.CommentRepository;
//import com.example.workoutmate.domain.follow.entity.Follow;
//import com.example.workoutmate.domain.follow.repository.FollowRepository;
//import com.example.workoutmate.domain.participation.entity.Participation;
//import com.example.workoutmate.domain.participation.enums.ParticipationState;
//import com.example.workoutmate.domain.participation.repository.ParticipationRepository;
//import com.example.workoutmate.domain.user.entity.User;
//import com.example.workoutmate.domain.user.enums.UserGender;
//import com.example.workoutmate.domain.user.enums.UserRole;
//import com.example.workoutmate.domain.user.repository.UserRepository;
//import com.example.workoutmate.domain.zzim.entity.Zzim;
//import com.example.workoutmate.domain.zzim.repository.ZzimRepository;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//@RequiredArgsConstructor
//public class initdata implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final BoardRepository boardRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final CommentRepository commentRepository;
//    private final ParticipationRepository participationRepository;
//    private final FollowRepository followRepository;
//    private final ZzimRepository zzimRepository;
//
//    private static final SportType[] SPORT_TYPES = SportType.values();
//    private static final int BATCH_SIZE = 1000; // 모든 엔티티에 일관 사용
//    private static final Random RANDOM = new Random();
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        String rawPassword = "pw";
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//
//        // 1. 유저 10만명 배치 저장
//        List<User> userList = new ArrayList<>();
//        for (int i = 1; i <= 100_000; i++) {
//            String email = String.format("user%05d@test.com", i);
//            User user = User.builder()
//                    .email(email)
//                    .password(encodedPassword)
//                    .name(String.format("user%05d", i))
//                    .gender(i % 2 == 0 ? UserGender.Male : UserGender.Female) // 대문자 확인!
//                    .role(UserRole.GUEST)
//                    .isDeleted(false)
//                    .isEmailVerified(true)
//                    .build();
//            userList.add(user);
//
//            if (i % BATCH_SIZE == 0) {
//                userRepository.saveAll(userList);
//                userList.clear();
//                System.out.println(i + "명 저장 완료!");
//            }
//        }
//        if (!userList.isEmpty()) userRepository.saveAll(userList);
//        System.out.println("== 테스트 유저 10만명 초기화 완료 ==");
//
//        // 2. 유저 전체를 가져와서 게시글 작성자 랜덤 배정
//        List<User> users = userRepository.findAll();
//
//        // 3. 게시글 10만개 배치 저장 (작성자 랜덤)
//        List<Board> boardList = new ArrayList<>();
//        for (int i = 1; i <= 100_000; i++) {
//            User writer = users.get(RANDOM.nextInt(users.size()));
//            SportType sportType = SPORT_TYPES[(i - 1) % SPORT_TYPES.length];
//            Board board = Board.builder()
//                    .writer(writer)
//                    .title("테스트 게시글 " + i)
//                    .content("JPA 배치 부하 테스트용 게시글입니다. 번호: " + i)
//                    .sportType(sportType)
//                    .maxParticipants((long) (RANDOM.nextInt(18) + 2)) // 2~20
//                    .currentParticipants(0L)
//                    .isDeleted(false)
//                    .status(Status.OPEN)
//                    .viewCount(0)
//                    .build();
//            boardList.add(board);
//
//            if (i % BATCH_SIZE == 0) {
//                boardRepository.saveAll(boardList);
//                boardList.clear();
//                System.out.println(i + "개 게시글 저장 완료!");
//            }
//        }
//        if (!boardList.isEmpty()) boardRepository.saveAll(boardList);
//        System.out.println("== 게시글 10만개 생성 완료 ==");
//
//        // 게시글/유저 재조회
//        List<Board> boards = boardRepository.findAll();
//
//        // 4. 댓글 10만개
//        List<Comment> commentList = new ArrayList<>();
//        for (int i = 0; i < 100_000; i++) {
//            User writer = users.get(RANDOM.nextInt(users.size()));
//            Board board = boards.get(RANDOM.nextInt(boards.size()));
//            Comment comment = Comment.builder()
//                    .writer(writer)
//                    .board(board)
//                    .content("댓글 내용 " + (i + 1))
//                    .isDeleted(false)
//                    .build();
//            commentList.add(comment);
//
//            if (commentList.size() == BATCH_SIZE) {
//                commentRepository.saveAll(commentList);
//                commentList.clear();
//            }
//        }
//        if (!commentList.isEmpty()) commentRepository.saveAll(commentList);
//        System.out.println("댓글 10만개 생성 완료");
//
//        // 5. Participation(참여) 10만개 (중복 방지)
//        Set<String> participationSet = new HashSet<>();
//        List<Participation> participationList = new ArrayList<>();
//        for (int i = 0; i < 100_000; i++) {
//            User applicant = users.get(RANDOM.nextInt(users.size()));
//            Board board = boards.get(RANDOM.nextInt(boards.size()));
//            String key = board.getId() + "_" + applicant.getId();
//            if (!participationSet.add(key)) {
//                i--;
//                continue;
//            }
//            Participation participation = Participation.builder()
//                    .board(board)
//                    .applicant(applicant)
//                    .state(ParticipationState.NONE)
//                    .build();
//            participationList.add(participation);
//
//            if (participationList.size() == BATCH_SIZE) {
//                participationRepository.saveAll(participationList);
//                participationList.clear();
//            }
//        }
//        if (!participationList.isEmpty()) participationRepository.saveAll(participationList);
//        System.out.println("Participation 10만개 생성 완료");
//
//        // 6. Zzim(찜) 10만개 (중복 방지)
//        Set<String> zzimSet = new HashSet<>();
//        List<Zzim> zzimList = new ArrayList<>();
//        for (int i = 0; i < 100_000; i++) {
//            User user = users.get(RANDOM.nextInt(users.size()));
//            Board board = boards.get(RANDOM.nextInt(boards.size()));
//            String key = board.getId() + "_" + user.getId();
//            if (!zzimSet.add(key)) {
//                i--;
//                continue;
//            }
//            Zzim zzim = Zzim.builder()
//                    .board(board)
//                    .user(user)
//                    .build();
//            zzimList.add(zzim);
//
//            if (zzimList.size() == BATCH_SIZE) {
//                zzimRepository.saveAll(zzimList);
//                zzimList.clear();
//            }
//        }
//        if (!zzimList.isEmpty()) zzimRepository.saveAll(zzimList);
//        System.out.println("Zzim 10만개 생성 완료");
//
//        // 7. Follow(팔로우) 10만개 (중복/자기자신 방지)
//        Set<String> followSet = new HashSet<>();
//        List<Follow> followList = new ArrayList<>();
//        for (int i = 0; i < 100_000; i++) {
//            User follower = users.get(RANDOM.nextInt(users.size()));
//            User following = users.get(RANDOM.nextInt(users.size()));
//            if (follower.getId().equals(following.getId())) {
//                i--;
//                continue;
//            }
//            String key = follower.getId() + "_" + following.getId();
//            if (!followSet.add(key)) {
//                i--;
//                continue;
//            }
//            Follow follow = new Follow(follower, following);
//            followList.add(follow);
//
//            if (followList.size() == BATCH_SIZE) {
//                followRepository.saveAll(followList);
//                followList.clear();
//            }
//        }
//        if (!followList.isEmpty()) followRepository.saveAll(followList);
//        System.out.println("Follow 10만개 생성 완료");
//    }
//}