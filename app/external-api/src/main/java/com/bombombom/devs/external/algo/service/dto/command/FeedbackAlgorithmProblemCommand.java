package com.bombombom.devs.external.algo.service.dto.command;

import lombok.Builder;

@Builder
public record FeedbackAlgorithmProblemCommand(
    Long studyId,
    Long problemId,
    Boolean again,
    Integer difficulty
) {

}
