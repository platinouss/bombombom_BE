package com.bombombom.devs.study.service.dto.result;

import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import jakarta.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AlgorithmStudyResult(
    Long id,
    String name,
    String introduce,
    int capacity,
    int headCount,
    int weeks,
    LocalDate startDate,
    int reliabilityLimit,
    int penalty,
    StudyStatus state,
    StudyType studyType,
    float difficultyMath,
    float difficultyGreedy,
    float difficultyImpl,
    float difficultyGraph,
    float difficultyGeometry,
    float difficultyDs,
    float difficultyString,
    float difficultyDp,
    int difficultyGap,
    int problemCount) {

}
