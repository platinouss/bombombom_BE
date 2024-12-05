package com.bombombom.devs.external.points.controller.dto;

import com.bombombom.devs.external.points.service.PointsHistoryResult;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record GetPointsHistoriesResponse(
    List<PointsHistoryResponse> pointsHistories
) {

    public static GetPointsHistoriesResponse of(List<PointsHistoryResponse> response) {
        return GetPointsHistoriesResponse.builder()
            .pointsHistories(response)
            .build();
    }

    @Builder
    public record PointsHistoryResponse(
        Long amount,
        String contents,
        LocalDateTime createdAt
    ) {

        public static PointsHistoryResponse fromResult(PointsHistoryResult result) {
            return PointsHistoryResponse.builder()
                .amount(result.amount())
                .contents(result.contents())
                .createdAt(result.createdAt())
                .build();
        }
    }
}
