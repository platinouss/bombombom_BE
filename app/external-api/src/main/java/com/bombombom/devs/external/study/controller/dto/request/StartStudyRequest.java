package com.bombombom.devs.external.study.controller.dto.request;

import com.bombombom.devs.external.study.service.dto.command.StartStudyCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record StartStudyRequest(
    @NotNull @Min(1) Long studyId
) {

    public StartStudyCommand toServiceDto() {
        return StartStudyCommand.builder()
            .studyId(studyId)
            .build();

    }

}
