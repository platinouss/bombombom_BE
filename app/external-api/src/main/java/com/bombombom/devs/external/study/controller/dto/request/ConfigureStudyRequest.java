package com.bombombom.devs.external.study.controller.dto.request;

import com.bombombom.devs.external.study.service.dto.command.ConfigureStudyCommand;

public record ConfigureStudyRequest(
    Boolean duplicated
) {


    public ConfigureStudyCommand toServiceDto() {
        return ConfigureStudyCommand.builder()
            .duplicated(duplicated)
            .build();
    }
}
