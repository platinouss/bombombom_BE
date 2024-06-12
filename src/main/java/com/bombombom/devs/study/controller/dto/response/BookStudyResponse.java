package com.bombombom.devs.study.controller.dto.response;

import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record BookStudyResponse (
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
    @JsonProperty("book_id")
    Long bookId)
    implements StudyResponse {

    public static BookStudyResponse of(BookStudyResult res) {

        return builder().name(res.name()).id(res.id()).introduce(res.introduce()).capacity(
            res.capacity()).headCount(res.headCount()).weeks(res.weeks()).startDate(res.startDate())
            .reliabilityLimit(res.reliabilityLimit()).penalty(res.penalty()).state(res.state())
            .studyType(res.studyType()).bookId(res.bookId()).build();

    }

}
