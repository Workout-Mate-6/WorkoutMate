package com.example.workoutmate.domain.recommend.dto;

import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.follow.entity.Follow;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.zzim.entity.Zzim;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Builder
public class UserActivityData {
    private List<Participation> participations;
    private List<Zzim> zzims;
    private List<Follow> friends;
    @Builder.Default
    private Set<SportType> preferredSportTypes= Collections.emptySet();
    @Builder.Default
    private Set<Integer> preferredHours = Collections.emptySet();

    // 캐싱을 위한 필드 추가
    private Set<Long> cachedZzimBoardIds;
    private Set<Long> cachedFriendIds;
    private Set<SportType> cachedPreferredSportTypes;
    private Set<Integer> cachedPreferredHours;

    public Set<Long> getFriendIds() {
        if (cachedFriendIds == null) {
            cachedFriendIds = friends != null ?
                    friends.stream()
                            .map(f -> f.getFollowing().getId())
                            .collect(Collectors.toSet()) :
                    Collections.emptySet();
        }
        return cachedFriendIds;
    }

    public Set<Long> getZzimBoardIds() {
        if (cachedZzimBoardIds == null) {
            cachedZzimBoardIds = zzims != null ?
                    zzims.stream()
                            .map(z -> z.getBoard().getId())
                            .collect(Collectors.toSet()) :
                    Collections.emptySet();
        }
        return cachedZzimBoardIds;
    }

    public Set<SportType> getPreferredSportTypes() {
        if (cachedPreferredSportTypes == null) {
            cachedPreferredSportTypes = calculatePreferredSportTypes();
        }
        return cachedPreferredSportTypes;
    }

    public Set<Integer> getPreferredHours() {
        if (cachedPreferredHours == null) {
            cachedPreferredHours = calculatePreferredHours();
        }
        return cachedPreferredHours;
    }

    // 개선된 선호 스포츠 타입 계산 (빈도 기반)
    private Set<SportType> calculatePreferredSportTypes() {
        if (participations == null || participations.isEmpty()) {
            return Collections.emptySet();
        }

        // 각 스포츠 타입별 참여 횟수 계산
        Map<SportType, Long> typeCount = participations.stream()
                .collect(Collectors.groupingBy(p -> p.getBoard().getSportType(), Collectors.counting()));

        // 최고 빈도의 30% 이상인 타입들만 "선호"로 간주
        long maxCount = typeCount.values().stream().mapToLong(Long::longValue).max().orElse(0);
        double threshold = maxCount * 0.3;

        return typeCount.entrySet().stream()
                .filter(entry -> entry.getValue() >= threshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    // 개선된 선호 시간대 계산 (빈도 기반 + null 안전)
    private Set<Integer> calculatePreferredHours() {
        if (participations == null || participations.isEmpty()) {
            return Collections.emptySet();
        }

        // null 체크 추가하여 안전하게 시간 추출
        Map<Integer, Long> hourCount = participations.stream()
                .filter(p -> p.getBoard() != null && p.getBoard().getStartTime() != null) // null 체크
                .map(p -> p.getBoard().getStartTime().getHour())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 최고 빈도의 30% 이상인 시간대들만 "선호"로 간주
        long maxCount = hourCount.values().stream().mapToLong(Long::longValue).max().orElse(0);
        double threshold = maxCount * 0.3;

        return hourCount.entrySet().stream()
                .filter(entry -> entry.getValue() >= threshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
