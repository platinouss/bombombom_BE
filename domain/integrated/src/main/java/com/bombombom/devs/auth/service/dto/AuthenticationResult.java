package com.bombombom.devs.auth.service.dto;

import lombok.Builder;

@Builder
public record AuthenticationResult(
    String accessToken,
    String refreshToken
) {

}
