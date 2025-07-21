package com.example.workoutmate.domain.user.service;

import com.example.workoutmate.domain.user.dto.UserEditRequestDto;
import com.example.workoutmate.domain.user.dto.UserEditResponseDto;
import com.example.workoutmate.domain.user.entity.User;
import com.example.workoutmate.domain.user.enums.UserGender;
import com.example.workoutmate.domain.user.repository.UserRepository;
import com.example.workoutmate.global.config.CustomUserPrincipal;
import com.example.workoutmate.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.workoutmate.global.enums.CustomErrorCode.PASSWORD_NOT_MATCHED;
import static com.example.workoutmate.global.enums.CustomErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    @Transactional
    public UserEditResponseDto editUserInfo(CustomUserPrincipal authUser, UserEditRequestDto requestDto) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            if(!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
                throw new CustomException(PASSWORD_NOT_MATCHED, PASSWORD_NOT_MATCHED.getMessage());
            }
            // 비밀번호 인코더 적용 후 설정
            // user.changePassword(requestDto.getPassword());
        }

        if(requestDto.getEmail() != null) {
            user.changeEmail(requestDto.getEmail());
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


    /* 도메인 관련 메서드 */

    public User findById(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
    }
}
