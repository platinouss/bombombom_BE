package com.bombombom.devs.external.study.service.dto.command;

import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.InvalidInputException;
import lombok.Builder;

@Builder
public record ConfigureStudyCommand(
    Boolean duplicated
) {

    public void assertAnyNotNull() {
        if (duplicated == null) {
            throw new InvalidInputException(ErrorCode.ALL_IS_NULL);
        }
    }
}
