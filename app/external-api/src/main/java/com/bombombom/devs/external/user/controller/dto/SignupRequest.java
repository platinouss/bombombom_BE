package com.bombombom.devs.external.user.controller.dto;

import com.bombombom.devs.user.service.dto.SignupCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignupRequest(
    @NotBlank(message = "공백일 수 없습니다.") String username,
    @NotBlank(message = "공백일 수 없습니다.") String password,
    @Size(max = 255, message = "255자를 넘을 수 없습니다.") String introduce
) {

    public SignupCommand toServiceDto() {
        return SignupCommand.builder()
            .username(username)
            .password(password)
            .introduce(introduce)
            .build();
    }
}
