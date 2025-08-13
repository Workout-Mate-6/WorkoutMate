package com.example.workoutmate.domain.user.service;

import com.example.workoutmate.domain.board.service.BoardSearchService;
import com.example.workoutmate.domain.follow.service.FollowCountService;
import com.example.workoutmate.domain.user.dto.UserEditRequestDto;
import com.example.workoutmate.domain.user.dto.UserEditResponseDto;
import com.example.workoutmate.domain.user.dto.UserInfoResponseDto;
import com.example.workoutmate.domain.user.dto.WithdrawRequestDto;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.entity.UserMapper;
import com.example.workoutmate.domain.user.enums.UserGender;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.workoutmate.global.enums.CustomErrorCode.PASSWORD_NOT_MATCHED;
import static com.example.workoutmate.global.enums.CustomErrorCode.USER_NOT_FOUND;

/**
 * 회원 정보 수정, 유저 탈퇴 기능 클래스
 *
 * @author 이현하
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardSearchService boardSearchService;
    private final FollowCountService followCountService;

    /**
     * 유저 정보 조회 (마이페이지)
     *
     * @param authUser 로그인한 유저 정보
     * @return 조회된 유저 정보
     */
    @Transactional(readOnly = true)
    public UserInfoResponseDto getMyInfo(CustomUserPrincipal authUser) {
        User user = findById(authUser.getId());

        int followerCount = followCountService.countByFollowingId(user.getId());
        int followingCount = followCountService.countByFollowerId(user.getId());
        int myBoardCount = boardSearchService.countBoardsByWriter(user.getId());

        return UserMapper.toUserInfoResponseDto(user, followerCount, followingCount, myBoardCount);
    }

    /**
     * 유저 정보 수정
     *
     * @param authUser 로그인한 유저 정보
     * @param requestDto 수정할 유저 정보
     * @return 수정된 유저 정보
     */
    @Transactional
    public UserEditResponseDto editUserInfo(CustomUserPrincipal authUser, UserEditRequestDto requestDto) {
        User user = findById(authUser.getId());

        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            if(!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
                throw new CustomException(PASSWORD_NOT_MATCHED, PASSWORD_NOT_MATCHED.getMessage());
            }
            String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
            user.changePassword(encodedPassword);
        }

        if(requestDto.getName() != null) {
            user.changeName(requestDto.getName());
        }
        if(requestDto.getGender() != null) {
            user.changeGender(UserGender.from(requestDto.getGender()));
        }

        userRepository.save(user);

        return new UserEditResponseDto(user);
    }


    /**
     * 유저 탈퇴
     *
     * @param authUser 로그인한 유저 정보
     * @param requestDto 비밀번호
     */
    @Transactional
    public void deleteUser(CustomUserPrincipal authUser, WithdrawRequestDto requestDto) {
        User user = findById(authUser.getId());

        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCHED, PASSWORD_NOT_MATCHED.getMessage());
        }

        user.delete();
    }



    /* 도메인 관련 메서드 */

    public User findById(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailAndIsDeletedFalseAndIsEmailVerifiedTrue(email).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
    }
}
