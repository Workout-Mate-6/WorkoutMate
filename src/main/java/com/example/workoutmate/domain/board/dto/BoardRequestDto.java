package com.example.workoutmate.domain.board.dto;

import com.example.workoutmate.domain.board.entity.SportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "운동 종목은 필수입니다.")
    private SportType sportType;

    @NotNull(message = "모집인원은 필수입니다.")
    @Positive(message = "모집인원은 0보다 커야합니다.")
    private Long maxParticipants;

    @NotNull(message = "운동 모임의 시간을 작성해주세요!")
    private LocalDateTime startTime;

    public BoardRequestDto(String title, String content, SportType sportType, Long maxParticipants, LocalDateTime startTime) {
        this.title = title;
        this.content = content;
        this.sportType = sportType;
        this.maxParticipants = maxParticipants;
        this.startTime = startTime;
    }
}
