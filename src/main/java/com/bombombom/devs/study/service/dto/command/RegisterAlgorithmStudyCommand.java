package com.bombombom.devs.study.service.dto.command;


import com.bombombom.devs.study.models.AlgorithmStudy;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record RegisterAlgorithmStudyCommand (
    @NotNull String name,
    @NotNull String introduce,
    int capacity,
    int weeks,
    @NotNull LocalDate startDate,
    int reliabilityLimit,
    int penalty,
    int difficultyBegin,
    int difficultyEnd,
    int problemCount){

    public AlgorithmStudy toEntity() {
        int difficultyGap = difficultyEnd-difficultyBegin;
        float db = difficultyBegin;

        return AlgorithmStudy.builder().name(name).introduce(introduce)
            .capacity(capacity).weeks(weeks).startDate(startDate)
            .reliabilityLimit(reliabilityLimit).penalty(penalty)
            .difficultyGraph(db).difficultyString(db).difficultyImpl(db)
            .difficultyMath(db).difficultyDp(db).difficultyGraph(db)
            .difficultyDs(db).difficultyGeometry(db)
            .difficultyGap(difficultyGap)
            .build();

    }

}
