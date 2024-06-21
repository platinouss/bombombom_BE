package com.bombombom.devs.study.controller.dto.request;


import static com.bombombom.devs.study.Constants.MAX_CAPACITY;
import static com.bombombom.devs.study.Constants.MAX_DIFFICULTY_LEVEL;
import static com.bombombom.devs.study.Constants.MAX_PENALTY;
import static com.bombombom.devs.study.Constants.MAX_PROBLEM_COUNT;
import static com.bombombom.devs.study.Constants.MAX_RELIABLITY_LIMIT;
import static com.bombombom.devs.study.Constants.MAX_WEEKS;

import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.service.dto.command.RegisterAlgorithmStudyCommand;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

@Builder
public record RegisterAlgorithmStudyRequest(
    @NotBlank @Size(max = 255, message = "스터디명은 255자를 넘을 수 없습니다.") String name,
    @NotBlank @Size(max = 500, message = "스터디소개는 500자를 넘을 수 없습니다.") String introduce,
    @NotNull @Range(min = 1, max = MAX_CAPACITY) Integer capacity,
    @NotNull @Range(min = 1, max = MAX_WEEKS) Integer weeks,
    @NotNull LocalDate startDate,
    @NotNull @Range(max = MAX_RELIABLITY_LIMIT) Integer reliabilityLimit,
    @NotNull @Range(max = MAX_PENALTY) Integer penalty,
    @NotNull @Range(max = MAX_DIFFICULTY_LEVEL) Integer difficultyBegin,
    @NotNull @Range(max = MAX_DIFFICULTY_LEVEL) Integer difficultyEnd,
    @NotNull @Range(min = 1, max = MAX_PROBLEM_COUNT) Integer problemCount
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
            .state(StudyStatus.READY)
            .headCount(0)
            .build();

    }

    @AssertTrue
    private boolean isDifficultyBeginLteDifficultyEnd() {
        return difficultyBegin <= difficultyEnd;
    }

    @AssertTrue
    private boolean isStartDateAfterOrEqualToday() {
        LocalDate now = LocalDate.now();
        return startDate.isAfter(now) || startDate.isEqual(now);
    }
}
