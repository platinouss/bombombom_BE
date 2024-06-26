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

record TagDisplayNames(
    String language,
    String name,
    String shortName
) {

}

record TagAlias(String alias) {

}

record ProblemTag(
    String key,
    boolean isMeta,
    int bojTagId,
    int problemCount,
    List<TagDisplayNames> displayNames,
    List<TagAlias> aliases
) {

}