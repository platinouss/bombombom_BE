package com.bombombom.devs.external.study.controller.dto.request;

import com.bombombom.devs.external.study.service.dto.command.DeleteAssignmentCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DeleteAssignmentRequest(
    @NotNull @Min(0) Integer roundIdx,
    @NotEmpty List<Long> assignmentIds

) {

    public DeleteAssignmentCommand toServiceDto() {
        return DeleteAssignmentCommand.builder()
            .roundIdx(roundIdx)
            .assignmentIds(assignmentIds)
            .build();

    }


}
