package com.bombombom.devs.external.study.controller.dto.request;

import com.bombombom.devs.external.study.service.dto.command.VoteAssignmentCommand;
import jakarta.validation.constraints.NotNull;

public record VoteAssignmentRequest(
    @NotNull Long first,
    Long second
) {

    public VoteAssignmentCommand toServiceDto() {
        return VoteAssignmentCommand.builder()
            .first(first)
            .second(second)
            .build();
    }
}
