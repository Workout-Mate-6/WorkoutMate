package com.example.workoutmate.global.config;

import com.example.workoutmate.domain.user.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserPrincipal implements UserDetails, Principal {

    private final Long id;
    private final String email;
    private final UserRole userRole;

    public CustomUserPrincipal(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> getUserRole().name());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // WebSocket에서 사용자 식별용
    @Override
    public String getName() {
        return email;
    }
}
