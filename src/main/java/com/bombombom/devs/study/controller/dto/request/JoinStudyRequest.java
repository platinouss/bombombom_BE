package com.bombombom.devs.study.controller.dto.request;


import com.bombombom.devs.study.service.dto.command.JoinStudyCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record JoinStudyRequest(
    @NotNull Long studyId
) {
    public JoinStudyCommand toServiceDto() {
        return JoinStudyCommand.builder()
            .studyId(studyId)
            .build();

    }
}
