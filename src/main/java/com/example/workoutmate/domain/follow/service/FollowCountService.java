package com.example.workoutmate.domain.follow.service;

import com.example.workoutmate.domain.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowCountService {

    private final FollowRepository followRepository;

    public int countByFollowingId(Long userId){
        return followRepository.countByFollowingId(userId);
    }

    public int countByFollowerId(Long userId){
        return followRepository.countByFollowerId(userId);
    }
}
