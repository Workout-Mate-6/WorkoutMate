package com.example.workoutmate.global.util;

import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.enums.UserGender;
import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TestInitData {

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        for (int i = 0; i < 10000; i++) {
            User user = User.builder()
                    .email("test" + i + "@test.com")
                    .password("test1234!") // 실제로는 해시해야 하지만 테스트용은 ok
                    .name("테스터" + i)
                    .gender(UserGender.Male) // 또는 랜덤하게
                    .role(UserRole.GUEST)
                    .isDeleted(false)
                    .isEmailVerified(false)
                    .build();
            userRepository.save(user);
        }
    }
}
