package com.bombombom.devs.external.user.controller.dto;

import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.user.model.Role;
import lombok.Builder;

@Builder
public record UserProfileResponse(
    Long id,
    String username,
    String image,
    String introduce,
    Integer reliability,
    Integer money,
    Role role
) {

    public static UserProfileResponse fromResult(UserProfileResult result) {
        return UserProfileResponse.builder()
            .id(result.id())
            .username(result.username())
            .role(result.role())
            .image(result.image())
            .introduce(result.introduce())
            .reliability(result.reliability())
            .money(result.money())
            .build();
    }
}
