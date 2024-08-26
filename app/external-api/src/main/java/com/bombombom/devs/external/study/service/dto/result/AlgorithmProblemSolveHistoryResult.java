package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.study.model.AlgorithmProblemSolveHistory;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AlgorithmProblemSolveHistoryResult(
    Long userId,
    Long problemId,
    LocalDateTime solvedAt,
    Integer tryCount
) {

    public static AlgorithmProblemSolveHistoryResult fromEntity(
        AlgorithmProblemSolveHistory history) {
        return AlgorithmProblemSolveHistoryResult.builder()
            .userId(history.getUser().getId())
            .problemId(history.getProblem().getId())
            .solvedAt(history.getSolvedAt())
            .tryCount(history.getTryCount())
            .build();
    }
}