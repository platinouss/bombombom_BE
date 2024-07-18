package com.bombombom.devs.external.study.service.dto.result.progress;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.study.model.AlgorithmProblemAssignmentSolveHistory;
import java.util.List;
import lombok.Builder;

@Builder
public record AlgorithmStudyProgress(
    List<AlgorithmProblem> algorithmProblems,
    List<AlgorithmProblemAssignmentSolveHistory> histories
) {

    public static AlgorithmStudyProgress fromEntity(
        List<AlgorithmProblem> algorithmProblems,
        List<AlgorithmProblemAssignmentSolveHistory> histories
    ) {
        return AlgorithmStudyProgress.builder()
            .algorithmProblems(algorithmProblems)
            .histories(histories)
            .build();
    }

}
