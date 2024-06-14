package com.bombombom.devs.study.controller.dto.response;

import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Column;
import java.time.LocalDate;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@JsonNaming(SnakeCaseStrategy.class)
public record AlgorithmStudyResponse(
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

    Float difficultyMath,

    Float difficultyDp,

    Float difficultyGreedy,

    Float difficultyImpl,

    Float difficultyGraph,

    Float difficultyGeometry,

    Float difficultyDs,

    Float difficultyString,

    Integer difficultyGap,

    Integer problemCount)
    implements StudyResponse {

    public static AlgorithmStudyResponse of(AlgorithmStudyResult res) {

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
            .difficultyDp(res.difficultyDp())
            .difficultyDs(res.difficultyDs())
            .difficultyGap(res.difficultyGap())
            .difficultyGeometry(res.difficultyGeometry())
            .difficultyMath(res.difficultyMath())
            .difficultyGreedy(res.difficultyGreedy())
            .difficultyString(res.difficultyString())
            .difficultyGraph(res.difficultyGraph())
            .difficultyImpl(res.difficultyImpl())
            .problemCount(res.problemCount())
            .build();

    }
}
