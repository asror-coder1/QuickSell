package com.quicksell.engine.security;

import com.quicksell.engine.user.UserRole;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class AuthenticatedUser implements UserDetails {

    private final UUID userId;
    private final UUID shopId;
    private final String username;
    private final String password;
    private final UserRole role;

    public AuthenticatedUser(UUID userId, UUID shopId, String username, String password, UserRole role) {
        this.userId = userId;
        this.shopId = shopId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
