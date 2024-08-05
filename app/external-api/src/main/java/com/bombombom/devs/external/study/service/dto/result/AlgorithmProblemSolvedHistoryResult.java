package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.study.model.AlgorithmProblemSolvedHistory;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AlgorithmProblemSolvedHistoryResult(
    Long userId,
    Long problemId,
    LocalDateTime solvedAt,
    Integer tryCount
) {

    public static AlgorithmProblemSolvedHistoryResult fromEntity(
        AlgorithmProblemSolvedHistory history) {
        return AlgorithmProblemSolvedHistoryResult.builder()
            .userId(history.getUser().getId())
            .problemId(history.getProblem().getId())
            .solvedAt(history.getSolvedAt())
            .tryCount(history.getTryCount())
            .build();
    }
}