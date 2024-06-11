package com.bombombom.devs.user.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignupRequest(
    @NotBlank(message = "공백일 수 없습니다.") String username,
    @NotBlank(message = "공백일 수 없습니다.") String password,
    @Size(max = 255, message = "255자를 넘을 수 없습니다.") String introduce
) {

}
