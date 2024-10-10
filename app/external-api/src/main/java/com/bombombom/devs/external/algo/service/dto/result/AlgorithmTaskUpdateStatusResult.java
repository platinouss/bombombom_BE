package com.bombombom.devs.external.algo.service.dto.result;

import com.bombombom.devs.algo.model.vo.AlgorithmTaskUpdateStatus;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record AlgorithmTaskUpdateStatusResult(
    Boolean isUpdating,
    Long updatedAt
) {

    public static Map<Long, AlgorithmTaskUpdateStatusResult> fromResponse(
        Map<Long, AlgorithmTaskUpdateStatus> taskUpdateStatusMap) {
        return taskUpdateStatusMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                entry -> AlgorithmTaskUpdateStatusResult.fromResponse(entry.getValue())));
    }

    private static AlgorithmTaskUpdateStatusResult fromResponse(AlgorithmTaskUpdateStatus status) {
        if (status == null) {
            return AlgorithmTaskUpdateStatusResult.builder()
                .isUpdating(false)
                .updatedAt(null)
                .build();
        }
        return AlgorithmTaskUpdateStatusResult.builder()
            .isUpdating(status.isUpdating())
            .updatedAt(status.statusUpdatedAt())
            .build();
    }
}
