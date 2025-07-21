package com.example.workoutmate.domain.user.entity;

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

@Getter
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(unique = true)
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.GUEST;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt = null;

    @NotBlank
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public User (String email, String password, String name, UserGender gender, UserRole role){
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.role = role;
    }
}