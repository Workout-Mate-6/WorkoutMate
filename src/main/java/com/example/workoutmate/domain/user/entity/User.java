package com.example.workoutmate.domain.user.entity;

import com.example.workoutmate.domain.follow.entity.Follow;
import com.example.workoutmate.domain.user.enums.UserGender;
import com.example.workoutmate.domain.user.enums.UserRole;
import com.example.workoutmate.global.entity.BaseEntity;
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

    @NotBlank
    @Column(unique = true)
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserGender gender;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @NotNull
    private UserRole role = UserRole.GUEST;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt = null;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    //follow 쪽 에서 사용
    @OneToMany(mappedBy = "follower")
    private List<Follow> followers;
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
}