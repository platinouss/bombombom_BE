package com.bombombom.devs.external.user.service.dto;

import com.bombombom.devs.user.model.Role;
import com.bombombom.devs.user.model.User;
import lombok.Builder;

@Builder
public record UserProfileResult(
    Long id,
    String username,
    String image,
    String introduce,
    Integer reliability,
    Integer money,
    Role role
) {

    public static UserProfileResult fromEntity(User user) {
        return UserProfileResult.builder()
            .id(user.getId())
            .username(user.getUsername())
            .role(user.getRole())
            .image(user.getImage())
            .introduce(user.getIntroduce())
            .reliability(user.getReliability())
            .money(user.getMoney())
            .build();
    }
}
