package com.example.workoutmate.domain.participation.controlloer;


import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping("/boards/{boardId}/comments/{commentId}/participations")
    public ResponseEntity<ApiResponse<Void>> requestApproval(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ) {
        participationService.requestApporval(boardId, commentId, authUser);
        return ApiResponse.success(HttpStatus.OK, "요청을 보냈습니다.", null);
    }
}
