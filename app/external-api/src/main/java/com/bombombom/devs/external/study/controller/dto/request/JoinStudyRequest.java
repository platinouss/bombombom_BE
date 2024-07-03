package com.bombombom.devs.external.study.controller.dto.request;


import com.bombombom.devs.study.service.dto.command.JoinStudyCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record JoinStudyRequest(
    @NotNull @Min(1) Long studyId
) {
    public JoinStudyCommand toServiceDto() {
        return JoinStudyCommand.builder()
            .studyId(studyId)
            .build();

    }
}
