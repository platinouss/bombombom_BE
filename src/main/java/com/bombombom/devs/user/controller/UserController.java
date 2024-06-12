package com.bombombom.devs.user.controller;

import com.bombombom.devs.user.controller.dto.SignupRequest;
import com.bombombom.devs.user.controller.dto.UserProfileResponse;
import com.bombombom.devs.user.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(Principal principal) {
        return ResponseEntity.ok().body(UserProfileResponse.fromResult(
            userService.findByUsername(principal.getName())));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupRequest) {
        userService.addUser(signupRequest.toServiceDto());
        return ResponseEntity.ok().build();
    }
}
