package com.example.workoutmate.domain.recommend.v3.initData;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
@ConfigurationProperties(prefix = "seed")
@Getter
@Setter
class SeedProperties {
    private boolean enabled = false;
    private int users = 5000;
    private int followsPerUser = 20;
    private int boards = 40000;
    private int maxParticipantsMin = 4;
    private int maxParticipantsMax = 20;
    private double nearFullRatio = 0.10;
    private double fullRatio = 0.05;
    private double zzimRatio = 0.02;
    // 보드당 수락 참여자 수를 currentParticipants에 맞추지만, 평균 자동 보정을 위해 남겨둠
    private int acceptParticipationPerBoard = 3;
    private int batchSize = 5000; // 대량 저장 배치 단위
}
