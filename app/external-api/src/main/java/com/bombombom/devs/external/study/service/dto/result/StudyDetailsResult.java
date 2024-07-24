package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.StudyStatus;
import com.bombombom.devs.study.model.StudyType;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record StudyDetailsResult(
    StudyType studyType,
    String name,
    String introduce,
    Integer headCount,
    Integer capacity,
    Integer penalty,
    Integer reliabilityLimit,
    LocalDate startDate,
    Integer weeks,
    Long leaderId,
    StudyStatus status,
    StudyProgressResult currentStudyProgress
) {

    public static StudyDetailsResult fromResult(Study study,
        StudyProgressResult currentStudyProgress) {
        return StudyDetailsResult.builder()
            .studyType(study.getStudyType())
            .name(study.getName())
            .introduce(study.getIntroduce())
            .headCount(study.getHeadCount())
            .capacity(study.getCapacity())
            .penalty(study.getPenalty())
            .reliabilityLimit(study.getReliabilityLimit())
            .startDate(study.getStartDate())
            .weeks(study.getWeeks())
            .leaderId(study.getLeader().getId())
            .status(study.getState())
            .currentStudyProgress(currentStudyProgress)
            .build();
    }
}
