package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.solvedac.dto.ProblemListResponse;
import com.bombombom.devs.solvedac.dto.ProblemResponse;
import com.bombombom.devs.user.model.User;
import java.util.List;
import lombok.Builder;

@Builder
public record SolvedAlgorithmProblemResult(
    String username,
    List<AlgorithmProblemResult> algorithmProblems,
    List<Integer> solvedProblemsRefId
) {

    public static SolvedAlgorithmProblemResult fromEntity(User user,
        List<AlgorithmProblem> problems, ProblemListResponse solvedProblemListResponse) {
        return SolvedAlgorithmProblemResult.builder()
            .username(user.getUsername())
            .algorithmProblems(problems.stream().map(AlgorithmProblemResult::fromEntity).toList())
            .solvedProblemsRefId(
                solvedProblemListResponse.items().stream().map(ProblemResponse::problemId).toList())
            .build();
    }
}
