package com.bombombom.devs.client.solvedac.dto;

import java.util.List;

public record ProblemResponse(
    Integer problemId,
    String titleKo,
    Integer acceptedUserCount,
    Integer level,
    Double averageTries,
    List<ProblemTag> tags
) {

}


