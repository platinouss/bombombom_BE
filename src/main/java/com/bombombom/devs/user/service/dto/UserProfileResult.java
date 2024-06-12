package com.bombombom.devs.user.service.dto;

import com.bombombom.devs.user.models.Role;
import com.bombombom.devs.user.models.User;
import lombok.Builder;

@Builder
public record UserProfileResult(
    Long id,
    String username,
    Role role) {

    public static UserProfileResult fromEntity(User user) {
        return UserProfileResult.builder()
            .id(user.getId())
            .username(user.getUsername())
            .role(user.getRole())
            .build();
    }
}
