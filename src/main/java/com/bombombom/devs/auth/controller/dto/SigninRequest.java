package com.bombombom.devs.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record
SigninRequest(
    @NotNull @NotBlank String username,
    @NotNull @NotBlank String password
) {

}
