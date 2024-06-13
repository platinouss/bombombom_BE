package com.bombombom.devs.auth.controller.dto;

import com.bombombom.devs.auth.service.dto.AuthenticationResult;

public record AuthenticationResponse(
    String AccessToken,
    String RefreshToken
) {
    public static AuthenticationResponse fromResult(AuthenticationResult result) {
        return new AuthenticationResponse(result.accessToken(), result.refreshToken());
    }
}
