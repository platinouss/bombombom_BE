package com.bombombom.devs.external.user.controller.dto;

import com.bombombom.devs.external.user.service.dto.SignupCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignupRequest(
    @NotBlank(message = "username은 공백일 수 없습니다.") String username,
    @NotBlank(message = "password는 공백일 수 없습니다.") String password,
    String baekjoonId,
    @Size(max = 255, message = "255자를 넘을 수 없습니다.") String introduce
) {

    public SignupCommand toServiceDto() {
        return SignupCommand.builder()
            .username(username)
            .password(password)
            .baekjoonId(baekjoonId)
            .introduce(introduce)
            .build();
    }
}
