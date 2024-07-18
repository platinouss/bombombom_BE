package com.bombombom.devs.external.user.service.dto;

import lombok.Builder;

@Builder
public record SignupCommand(
    String username,
    String password,
    String introduce
) {

}
