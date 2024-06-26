package com.bombombom.devs.client.solvedac.dto;

import java.util.List;

public record ProblemListResponse(
    List<ProblemResponse> items
) {
}
