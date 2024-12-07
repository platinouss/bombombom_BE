package com.bombombom.devs.external.study.controller.dto.request;

import com.bombombom.devs.external.study.service.dto.command.CheckAlgorithmProblemSolvedCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CheckAlgorithmProblemSolvedRequest(
    @NotNull Long studyId,
    @NotNull Integer roundIdx,
    @Size(min = 1) Set<Long> problemIds,
    @NotNull Long userId
) {

    public CheckAlgorithmProblemSolvedCommand toServiceDto() {
        return CheckAlgorithmProblemSolvedCommand.builder()
            .studyId(studyId)
            .roundIdx(roundIdx)
            .problemIds(problemIds)
            .userId(userId)
            .build();
    }
}
