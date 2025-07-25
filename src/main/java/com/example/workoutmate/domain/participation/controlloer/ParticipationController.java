package com.example.workoutmate.domain.participation.controlloer;


import com.example.workoutmate.domain.participation.dto.ParticipationByBoardResponseDto;
import com.example.workoutmate.domain.participation.dto.ParticipationRequestDto;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @PatchMapping("/boards/{boardId}/approvalstatus/participation/{participationId}")
    public ResponseEntity<ApiResponse<Void>> decideApproval(
            @PathVariable Long boardId,
            @PathVariable Long participationId,
            @Valid @RequestBody ParticipationRequestDto participationRequestDto,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ) {
        participationService.decideApproval(boardId, participationId, participationRequestDto, authUser);
        return ApiResponse.success(HttpStatus.OK, "" + participationRequestDto, null);
    }

    // todo 조회기능 만들기
    @GetMapping("/participation")
    public ResponseEntity<ApiResponse<Page<ParticipationByBoardResponseDto>>> viewApproval(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody(required = false) ParticipationRequestDto state,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ) {
        Page<ParticipationByBoardResponseDto> response =
                participationService.viewApproval(page, size, state, authUser);

        return ApiResponse.success(HttpStatus.OK, "조회 되었습니다.", response);
    }
}
