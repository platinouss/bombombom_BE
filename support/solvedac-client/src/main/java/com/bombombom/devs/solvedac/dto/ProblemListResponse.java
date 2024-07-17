package com.bombombom.devs.solvedac.dto;

import java.util.List;

public record ProblemListResponse(
    List<ProblemResponse> items
) {
}
