package com.bombombom.devs.external.points.service;

import com.bombombom.devs.points.model.PointsHistory;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PointsHistoryResult(
    Long amount,
    String contents,
    LocalDateTime createdAt
) {

    public static PointsHistoryResult fromEntity(PointsHistory history) {
        return PointsHistoryResult.builder()
            .amount(history.getAmount())
            .contents(history.getContents())
            .createdAt(history.getCreatedAt())
            .build();
    }

}
