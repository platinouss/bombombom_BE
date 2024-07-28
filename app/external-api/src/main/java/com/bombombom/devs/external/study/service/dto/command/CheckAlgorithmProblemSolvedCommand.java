package com.bombombom.devs.external.study.service.dto.command;

import java.util.List;
import lombok.Builder;

@Builder
public record CheckAlgorithmProblemSolvedCommand(
    Long studyId,
    Integer roundIdx,
    List<Long> problemsId,
    Long userId
) {

}
