package com.example.workoutmate.domain.recommend.dto;

import com.example.workoutmate.domain.board.entity.SportType;
import com.example.workoutmate.domain.follow.entity.Follow;
import com.example.workoutmate.domain.participation.entity.Participation;
import com.example.workoutmate.domain.zzim.entity.Zzim;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Set;
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

    public Set<Long> getFriendIds() {
        return friends.stream().map(f->f.getFollowing().getId()).collect(Collectors.toSet());
    }

    public Set<SportType> getPreferredSportTypes() {
        // participation 기반으로 선호 운동 타입 추출
        return participations.stream()
                .map(p -> p.getBoard().getSportType())
                .collect(Collectors.toSet());
    }

    public Set<Integer> getPreferredHours() {
        // participation 기반으로 주로 참여한 시간대 추출
        return participations.stream()
                .map(p -> p.getBoard().getStartTime().getHour())
                .collect(Collectors.toSet());
    }

}
