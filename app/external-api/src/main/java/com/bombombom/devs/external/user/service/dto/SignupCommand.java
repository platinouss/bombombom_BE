package com.bombombom.devs.external.user.service.dto;

import com.bombombom.devs.user.model.Role;
import com.bombombom.devs.user.model.User;
import lombok.Builder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
public record SignupCommand(
    String username,
    String password,
    String baekjoonId,
    String introduce
) {

    public User toEntity(PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode(password);
        return User.builder()
            .username(username)
            .password(encodedPassword)
            .introduce(introduce)
            .baekjoon(baekjoonId)
            .role(Role.USER)
            .reliability(0)
            .money(0)
            .build();
    }
}
