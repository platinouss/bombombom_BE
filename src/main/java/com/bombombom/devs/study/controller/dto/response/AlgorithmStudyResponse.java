package com.bombombom.devs.study.controller.dto.response;

import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record AlgorithmStudyResponse(
    Long id,
    String name,
    String introduce,
    int capacity,
    @JsonProperty("head_count")
    int headCount,
    int weeks,
    @JsonProperty("start_date")
    LocalDate startDate,
    @JsonProperty("reliability_limit")
    int reliabilityLimit,
    int penalty,
    StudyStatus state,
    @JsonProperty("study_type")
    StudyType studyType,

    @JsonProperty("difficulty_math")
    float difficultyMath,

    @JsonProperty("difficulty_dp")
    float difficultyDp,

    @JsonProperty("difficulty_greedy")
    float difficultyGreedy,

    @JsonProperty("difficulty_impl")
    float difficultyImpl,

    @JsonProperty("difficulty_graph")
    float difficultyGraph,

    @JsonProperty("difficulty_geometry")
    float difficultyGeometry,

    @JsonProperty("difficulty_ds")
    float difficultyDs,

    @JsonProperty("difficulty_string")
    float difficultyString,

    @JsonProperty("difficulty_gap")
    int difficultyGap,

    @JsonProperty("problem_count")
    int problemCount)
    implements StudyResponse {

    public static AlgorithmStudyResponse of(AlgorithmStudyResult res) {

        return builder().name(res.name()).id(res.id()).introduce(res.introduce()).capacity(
                res.capacity()).headCount(res.headCount()).weeks(res.weeks()).startDate(res.startDate())
            .reliabilityLimit(res.reliabilityLimit()).penalty(res.penalty()).state(res.state())
            .studyType(res.studyType()).difficultyDp(res.difficultyDp())
            .difficultyDs(res.difficultyDs())
            .difficultyGap(res.difficultyGap()).difficultyGeometry(res.difficultyGeometry())
            .difficultyMath(res.difficultyMath()).difficultyGreedy(res.difficultyGreedy())
            .difficultyString(res.difficultyString()).difficultyGraph(res.difficultyGraph())
            .difficultyImpl(res.difficultyImpl()).problemCount(res.problemCount())
            .build();

    }
}
