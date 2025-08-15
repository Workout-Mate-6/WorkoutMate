package com.example.workoutmate.domain.participation.controlloer;


import com.example.workoutmate.domain.participation.dto.ParticipationAttendResponseDto;
import com.example.workoutmate.domain.participation.dto.ParticipationByBoardResponseDto;
import com.example.workoutmate.domain.participation.dto.ParticipationRequestDto;
import com.example.workoutmate.domain.participation.service.ParticipationService;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ParticipationController {

    private final ParticipationService participationService;


    // 댓글 작성자용
    // 요청 보내기
    @PatchMapping("/boards/{boardId}/participations-request")
    public ResponseEntity<ApiResponse<Void>> requestApproval(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ) {
        participationService.requestApporval(boardId, authUser);
        return ApiResponse.success(HttpStatus.OK, "요청을 보냈습니다.", null);
    }

    // 게시글 작성자용
    // 요청 받은거 (수락/거절하기)
    @PatchMapping("/boards/{boardId}/approvalstatus/participations/{participationId}")
    public ResponseEntity<ApiResponse<Void>> decideApproval(
            @PathVariable Long boardId,
            @PathVariable Long participationId,
            @Valid @RequestParam ParticipationRequestDto state,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ) {
        participationService.decideApproval(boardId, participationId, state, authUser);
        return ApiResponse.success(HttpStatus.OK, "" + state, null);
    }


    // 본인 게시글의 신청자 (전체) 조회 <state 값은 필수 아님>
    @GetMapping("/participations")
    public ResponseEntity<ApiResponse<Page<ParticipationByBoardResponseDto>>> viewApproval(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Valid @RequestParam(required = false) ParticipationRequestDto state,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ) {
        Page<ParticipationByBoardResponseDto> response =
                participationService.viewApproval(page, size, state, authUser);

        return ApiResponse.success(HttpStatus.OK, "조회 되었습니다.", response);
    }

    // 운동파티 조회(게시글)
    @GetMapping("/boards/{boardId}/participations/attends")
    public ResponseEntity<ApiResponse<List<ParticipationAttendResponseDto>>> viewAttends(
            @PathVariable Long boardId
    ) {
        return ApiResponse.success(
                HttpStatus.OK,
                "구성원들을 조회하였습니다.",
                participationService.viewAttends(boardId)
        );
    }

    // 불참만 가능하게
    @PatchMapping("/boards/{boardId}/participations-decline")
    public ResponseEntity<ApiResponse<Void>> cancelParticipation(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserPrincipal authUser
    ) {
        participationService.cancelParticipation(boardId,  authUser);
        return ApiResponse.success(HttpStatus.OK, "불참 되었습니다.", null);
    }
}
