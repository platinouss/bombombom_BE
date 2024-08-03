package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.study.service.dto.result.AlgorithmProblemResult;
import com.bombombom.devs.external.study.service.dto.result.SolvedAlgorithmProblemResult;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record AlgorithmStudyTaskStatusResponse(
    String username,
    Map<Long, Boolean> tasks
) {

    public static AlgorithmStudyTaskStatusResponse fromResult(UserProfileResult result,
        Map<Long, Boolean> tasks) {
        return AlgorithmStudyTaskStatusResponse.builder()
            .username(result.username())
            .tasks(tasks)
            .build();
    }

    public static AlgorithmStudyTaskStatusResponse fromResult(SolvedAlgorithmProblemResult result) {
        Map<Integer, Long> refIdToIdMap = result.algorithmProblems().stream()
            .collect(Collectors.toMap(AlgorithmProblemResult::refId, AlgorithmProblemResult::id));
        Map<Long, Boolean> taskStatus = result.algorithmProblems().stream()
            .collect(Collectors.toMap(AlgorithmProblemResult::id, (study) -> false));
        result.solvedProblemRefIds()
            .forEach(refId -> taskStatus.put(refIdToIdMap.get(refId), true));
        return AlgorithmStudyTaskStatusResponse.builder()
            .username(result.username())
            .tasks(taskStatus)
            .build();
    }
}