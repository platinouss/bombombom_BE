package com.bombombom.devs.external.study.service.dto.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record DeleteAssignmentCommand(
    @NotNull @Min(0) Integer roundIdx,
    @NotEmpty List<Long> assignmentIds) {

}
