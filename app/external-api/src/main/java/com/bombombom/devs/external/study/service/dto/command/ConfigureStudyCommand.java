package com.bombombom.devs.external.study.service.dto.command;

import lombok.Builder;

@Builder
public record ConfigureStudyCommand(
    Boolean duplicated
) {


}
