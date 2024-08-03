package com.bombombom.devs.external.study.service.dto.result.progress;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.external.algo.service.dto.result.AlgorithmProblemResult;
import com.bombombom.devs.external.algo.service.dto.result.AlgorithmProblemSolveHistoryResult;
import com.bombombom.devs.external.study.service.dto.result.RoundResult;
import com.bombombom.devs.study.model.AlgorithmProblemSolvedHistory;
import com.bombombom.devs.study.model.Round;
import java.util.List;
import lombok.Builder;

@Builder
public record AlgorithmStudyProgress(
    RoundResult round,
    List<AlgorithmProblemResult> algorithmProblems,
    List<AlgorithmProblemSolveHistoryResult> histories
) implements StudyProgress {

    public static AlgorithmStudyProgress fromEntity(
        Round round,
        List<AlgorithmProblem> algorithmProblems,
        List<AlgorithmProblemSolvedHistory> histories
    ) {
        return AlgorithmStudyProgress.builder()
            .round(RoundResult.fromEntity(round))
            .algorithmProblems(
                algorithmProblems.stream().map(AlgorithmProblemResult::fromEntity).toList())
            .histories(
                histories.stream().map(AlgorithmProblemSolveHistoryResult::fromEntity).toList())
            .build();
    }

}
