package com.example.workoutmate.global.scheduler;

import com.example.workoutmate.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class UnverifiedUserDeleteScheduler {

    private final AuthService authService;

    @Scheduled(fixedRate = 5 * 60 * 1000) // 5분 마다 실행
    public void unverifiedUserDelete(){

        authService.findUnverifiedUsersAndDelete(LocalDateTime.now().minusMinutes(30));

        // 예시 ->  LocalDateTime.now() 현재시각 = 13:30,  LocalDateTime.now().minusMinutes(30) = 13:00
    }
}
