package com.bombombom.devs.study.service.dto.result;

import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record BookStudyResult(
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
    Long bookId
) implements StudyResult {

}