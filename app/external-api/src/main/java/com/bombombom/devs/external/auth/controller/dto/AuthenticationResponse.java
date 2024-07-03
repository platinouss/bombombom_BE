package com.bombombom.devs.external.auth.controller.dto;

import com.bombombom.devs.external.auth.service.dto.AuthenticationResult;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationResponse(
    @JsonProperty("access_token")
    String accessToken,
    @JsonProperty("refresh_token")
    String refreshToken
) {
    public static AuthenticationResponse fromResult(AuthenticationResult result) {
        return new AuthenticationResponse(result.accessToken(), result.refreshToken());
    }
}
