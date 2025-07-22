package com.example.workoutmate.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequestDto {

    @NotBlank(message = "공백으로 입력을 할 수 없습니다.")
    private String content;

}
