package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.algo.service.dto.result.AlgorithmTaskUpdateStatusResult;
import com.bombombom.devs.external.study.service.dto.result.MemberInfoResult;
import java.util.Map;
import lombok.Builder;

@Builder
public record AlgorithmStudyTaskStatusResponse(
    String username,
    String baekjoonId,
    Boolean isUpdating,
    Long taskStatusUpdatedAt,
    Map<Long, Boolean> tasks
) {

    public static AlgorithmStudyTaskStatusResponse fromResult(MemberInfoResult memberInfoResult,
        AlgorithmTaskUpdateStatusResult taskUpdateStatusResult, Map<Long, Boolean> tasks) {
        return AlgorithmStudyTaskStatusResponse.builder()
            .username(memberInfoResult.username())
            .isUpdating(taskUpdateStatusResult.isUpdating())
            .taskStatusUpdatedAt(taskUpdateStatusResult.updatedAt())
            .tasks(tasks)
            .build();
    }
}