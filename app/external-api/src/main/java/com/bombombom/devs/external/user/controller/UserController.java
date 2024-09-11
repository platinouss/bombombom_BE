package com.bombombom.devs.external.user.controller;

import com.bombombom.devs.external.global.web.LoginUser;
import com.bombombom.devs.external.user.controller.dto.SignupRequest;
import com.bombombom.devs.external.user.controller.dto.UserProfileResponse;
import com.bombombom.devs.external.user.service.UserService;
import com.bombombom.devs.security.AppUserDetails;
import jakarta.validation.Valid;
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
    public ResponseEntity<UserProfileResponse> me(@LoginUser AppUserDetails userDetails) {
        return ResponseEntity.ok().body(UserProfileResponse.fromResult(
            userService.findById(userDetails.getId())));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupRequest) {
        userService.addUser(signupRequest.toServiceDto());
        return ResponseEntity.ok().build();
    }
}
