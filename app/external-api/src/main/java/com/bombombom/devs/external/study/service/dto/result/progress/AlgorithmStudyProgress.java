package com.bombombom.devs.external.study.service.dto.result.progress;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.study.model.AlgorithmProblemAssignmentSolveHistory;
import com.bombombom.devs.study.model.Round;
import java.util.List;
import lombok.Builder;

@Builder
public record AlgorithmStudyProgress(
    Round round,
    List<AlgorithmProblem> algorithmProblems,
    List<AlgorithmProblemAssignmentSolveHistory> histories
) {

    public static AlgorithmStudyProgress fromEntity(
        Round round,
        List<AlgorithmProblem> algorithmProblems,
        List<AlgorithmProblemAssignmentSolveHistory> histories
    ) {
        return AlgorithmStudyProgress.builder()
            .round(round)
            .algorithmProblems(algorithmProblems)
            .histories(histories)
            .build();
    }

}
