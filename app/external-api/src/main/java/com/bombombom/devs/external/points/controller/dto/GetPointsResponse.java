package com.bombombom.devs.external.points.controller.dto;

import lombok.Builder;

@Builder
public record GetPointsResponse(
    Long points
) {

    public static GetPointsResponse fromResult(Long points) {
        return GetPointsResponse.builder()
            .points(points)
            .build();
    }

}
