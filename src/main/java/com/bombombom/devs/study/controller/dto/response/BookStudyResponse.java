package com.bombombom.devs.study.controller.dto.response;

import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
public record BookStudyResponse(
    Long id,
    String name,
    String introduce,
    Integer capacity,
    Integer headCount,
    Integer weeks,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate,
    Integer reliabilityLimit,
    Integer penalty,
    StudyStatus state,
    StudyType studyType,
    Long bookId)
    implements StudyResponse {

    public static BookStudyResponse of(BookStudyResult res) {

        return builder()
            .id(res.id())
            .name(res.name())
            .introduce(res.introduce())
            .capacity(res.capacity())
            .headCount(res.headCount())
            .weeks(res.weeks())
            .startDate(res.startDate())
            .reliabilityLimit(res.reliabilityLimit())
            .penalty(res.penalty())
            .state(res.state())
            .studyType(res.studyType())
            .bookId(res.bookId())
            .build();

    }

}
