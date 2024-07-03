package com.bombombom.devs.external.auth.controller;

import com.bombombom.devs.external.auth.controller.dto.AuthenticationResponse;
import com.bombombom.devs.external.auth.controller.dto.SigninRequest;
import com.bombombom.devs.external.auth.service.AuthService;
import com.bombombom.devs.external.auth.service.dto.AuthenticationResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<AuthenticationResponse> authenticate(
        @Valid @RequestBody SigninRequest request) {
        AuthenticationResult result = authService.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                request.username(), request.password()));
        return ResponseEntity.ok((AuthenticationResponse.fromResult(result)));
    }
}
