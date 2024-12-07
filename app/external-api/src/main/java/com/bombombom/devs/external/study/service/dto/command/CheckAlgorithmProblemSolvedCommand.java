package com.bombombom.devs.external.study.service.dto.command;

import com.bombombom.devs.algo.model.vo.UpdateAlgorithmTaskStatusMessage;
import java.util.Set;
import lombok.Builder;

@Builder
public record CheckAlgorithmProblemSolvedCommand(
    Long userId,
    Long studyId,
    Integer roundIdx,
    Set<Long> problemIds
) {

    public UpdateAlgorithmTaskStatusMessage toVo() {
        return UpdateAlgorithmTaskStatusMessage.builder()
            .userId(userId)
            .studyId(studyId)
            .problemIds(problemIds)
            .build();
    }
}
