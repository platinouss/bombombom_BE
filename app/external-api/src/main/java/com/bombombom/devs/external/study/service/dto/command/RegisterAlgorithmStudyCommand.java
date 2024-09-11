package com.bombombom.devs.external.study.service.dto.command;


import com.bombombom.devs.study.enums.StudyStatus;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record RegisterAlgorithmStudyCommand(
    String name,
    String introduce,
    Integer capacity,
    Integer weeks,
    LocalDate startDate,
    Integer reliabilityLimit,
    Integer penalty,
    StudyStatus state,
    Integer headCount,
    Integer difficultyBegin,
    Integer difficultyEnd,
    Integer problemCount
) implements RegisterStudyCommand {


}
