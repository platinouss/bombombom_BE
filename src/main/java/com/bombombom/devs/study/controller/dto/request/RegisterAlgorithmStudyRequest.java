package com.bombombom.devs.study.controller.dto.request;


import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.service.dto.command.RegisterAlgorithmStudyCommand;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RegisterAlgorithmStudyRequest(
    @NotNull String name,
    @NotNull String introduce,

    Integer capacity,
    Integer weeks,
    @NotNull LocalDate startDate,
    Integer reliabilityLimit,
    Integer penalty,
    Integer difficultyBegin,
    Integer difficultyEnd,
    Integer problemCount
) {

    public RegisterAlgorithmStudyCommand toServiceDto() {

        return RegisterAlgorithmStudyCommand.builder()
            .name(name)
            .introduce(introduce)
            .capacity(capacity)
            .weeks(weeks)
            .startDate(startDate)
            .reliabilityLimit(reliabilityLimit)
            .penalty(penalty)
            .difficultyBegin(difficultyBegin)
            .difficultyEnd(difficultyEnd)
            .problemCount(problemCount)
            .build();

    }

}
