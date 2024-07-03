package com.bombombom.devs.external.auth.service.dto;

import lombok.Builder;

@Builder
public record AuthenticationResult(
    String accessToken,
    String refreshToken
) {

}
