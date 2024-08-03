package com.bombombom.devs.external.algo.service.dto.result;

import com.bombombom.devs.study.model.AlgorithmProblemSolvedHistory;
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
        AlgorithmProblemSolvedHistory history) {
        return AlgorithmProblemSolveHistoryResult.builder()
            .userId(history.getUser().getId())
            .problemId(history.getProblem().getId())
            .solvedAt(history.getSolvedAt())
            .tryCount(history.getTryCount())
            .build();
    }
}