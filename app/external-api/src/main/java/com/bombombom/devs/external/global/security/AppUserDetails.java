package com.bombombom.devs.external.global.security;

import com.bombombom.devs.user.model.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
@ToString(exclude = "password")
public class AppUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public static AppUserDetails fromUser(User user) {
        return AppUserDetails.builder()
            .id(user.getId())
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(List.of(new SimpleGrantedAuthority(user.getRole().name())))
            .build();
    }
}
