package com.bombombom.devs.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record SigninRequest(
    @NotBlank String username,
    @NotBlank String password
) {

}
