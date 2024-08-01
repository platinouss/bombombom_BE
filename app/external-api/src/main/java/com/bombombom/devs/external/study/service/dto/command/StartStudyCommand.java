package com.bombombom.devs.external.study.service.dto.command;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record StartStudyCommand(
    @NotNull Long studyId
) {

}
