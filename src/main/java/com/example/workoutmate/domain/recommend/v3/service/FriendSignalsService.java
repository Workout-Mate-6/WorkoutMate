package com.example.workoutmate.domain.recommend.v3.service;

import com.example.workoutmate.domain.board.entity.Board;
import com.example.workoutmate.domain.follow.service.FollowService;
import com.example.workoutmate.domain.participation.enums.ParticipationState;
import com.example.workoutmate.domain.participation.repository.QParticipationRepository;
import com.example.workoutmate.domain.recommend.v3.config.RecommendationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendSignalsService {
    private final FollowService followService;
    private final QParticipationRepository qParticipationRepository;
    private final RecommendationProperties props;

    /** 게시글별 친구 수 */
    public Map<Long, Integer> friendCounts(Long userId, List<Board> boards) {
        List<Long> followings = followService.getFollowingUserIds(userId);
        if (followings == null || followings.isEmpty()) {
            return boards.stream().collect(Collectors.toMap(Board::getId, b -> 0));
        }
        List<Long> boardIds = boards.stream().map(Board::getId).toList();
        Map<Long, Set<Long>> participantsByBoard = qParticipationRepository.findParticipantIdsByBoardIds(boardIds, ParticipationState.ACCEPTED);
        Map<Long, Integer> out = new HashMap<>();
        for (Board b : boards) {
            Set<Long> p = participantsByBoard.getOrDefault(b.getId(), Collections.emptySet());
            int c = 0;
            for (Long uid : p) if (followings.contains(uid)) c++;
            out.put(b.getId(), c);
        }
        return out;
    }

    /** 친구 탐색 타입(최근 exploreDays 내 친구들이 해본 다른 운동 타입) */
    public Set<String> friendExploreTypes(Long userId) {
        List<Long> followings = followService.getFollowingUserIds(userId);
        if (followings == null || followings.isEmpty()) return Set.of();
        LocalDate from = LocalDate.now().minusDays(props.getFriend().getExploreDays());
        return qParticipationRepository.findSportTypesByApplicantsAndDate(new HashSet<>(followings), from, ParticipationState.ACCEPTED);
    }
}
