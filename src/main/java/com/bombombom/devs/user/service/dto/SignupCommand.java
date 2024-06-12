package com.bombombom.devs.user.service.dto;

import lombok.Builder;

@Builder
public record SignupCommand(
        String username,
        String password,
        String introduce
) {
}
