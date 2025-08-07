package com.example.workoutmate.domain.user.entity;

import com.example.workoutmate.domain.follow.entity.Follow;
import com.example.workoutmate.domain.user.enums.UserGender;
import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.global.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserGender gender;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private UserRole role = UserRole.GUEST;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt = null;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expires_at")
    private LocalDateTime verificationCodeExpiresAt;

    @Column(name = "is_email_verified")
    @Builder.Default
    private boolean isEmailVerified = false;


    //follow 쪽 에서 사용
    @JsonIgnore
    @OneToMany(mappedBy = "follower")
    private List<Follow> followers;
    @JsonIgnore
    @OneToMany(mappedBy = "following")
    private List<Follow> followings;
    //

    public User (String email, String password, String name, UserGender gender, UserRole role){
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.role = role;
        this.isDeleted = false;
    }

    // 유저 이메일 변경
    public void changeEmail(String email) {
        this.email = email;
    }

    // 유저 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }

    // 유저 이름 변경
    public void changeName(String name) {
        this.name = name;
    }

    // 유저 성별 변경
    public void changeGender(UserGender gender) {
        this.gender = gender;
    }

    // 유저 탈퇴
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // 인증코드 발급 및 만료시간 세팅
    public void issueVerificationCode(String code, LocalDateTime expiresAt) {
        this.verificationCode = code;
        this.verificationCodeExpiresAt = expiresAt;
    }

    // 인증코드 성공적으로 검증(인증 완료)
    public void completeEmailVerification() {
        this.isEmailVerified = true;
        this.verificationCode = null;
        this.verificationCodeExpiresAt = null;
    }
}