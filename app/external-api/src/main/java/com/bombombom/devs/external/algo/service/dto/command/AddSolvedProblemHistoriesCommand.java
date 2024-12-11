package com.bombombom.devs.external.algo.service.dto.command;

import com.bombombom.devs.algo.model.vo.UpdateAlgorithmTaskStatusMessage;
import com.bombombom.devs.external.study.service.dto.command.CheckAlgorithmProblemSolvedCommand;
import java.util.Set;
import lombok.Builder;

@Builder
public record AddSolvedProblemHistoriesCommand(
    Long userId,
    Long studyId,
    Set<Long> problemIds
) {

    public static AddSolvedProblemHistoriesCommand of(CheckAlgorithmProblemSolvedCommand command) {
        return AddSolvedProblemHistoriesCommand.builder()
            .userId(command.userId())
            .problemIds(command.problemIds())
            .build();
    }

    public static AddSolvedProblemHistoriesCommand fromMessage(
        UpdateAlgorithmTaskStatusMessage message) {
        return AddSolvedProblemHistoriesCommand.builder()
            .userId(message.userId())
            .studyId(message.studyId())
            .problemIds(message.problemIds())
            .build();
    }
}
