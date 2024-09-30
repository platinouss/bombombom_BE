package com.bombombom.devs.algo.model.vo;

import java.util.Set;
import lombok.Builder;

@Builder
public record UpdateAlgorithmTaskStatus(
    Long studyId,
    Long userId,
    String baekjoonId,
    Set<Integer> problemRefIds
) {

    public static UpdateAlgorithmTaskStatus of(Long studyId, Long userId, String baekjoonId,
        Set<Integer> problemRefIds) {
        return UpdateAlgorithmTaskStatus.builder()
            .studyId(studyId)
            .userId(userId)
            .baekjoonId(baekjoonId)
            .problemRefIds(problemRefIds)
            .build();
    }

}
