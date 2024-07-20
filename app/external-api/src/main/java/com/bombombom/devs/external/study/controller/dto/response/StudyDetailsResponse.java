package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.StudyStatus;
import com.bombombom.devs.study.model.StudyType;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record StudyDetailsResponse(
    StudyDetails details,
    StudyProgressResponse round
) {

    @Builder
    public record StudyDetails(
        StudyType studyType,
        String name,
        String introduce,
        Integer headCount,
        Integer capacity,
        Integer penalty,
        Integer reliabilityLimit,
        LocalDate startDate,
        Integer weeks,
        StudyStatus status
    ) {

        public static StudyDetails fromResult(Study study) {
            return StudyDetails.builder()
                .studyType(study.getStudyType())
                .name(study.getName())
                .introduce(study.getIntroduce())
                .headCount(study.getHeadCount())
                .capacity(study.getCapacity())
                .penalty(study.getPenalty())
                .reliabilityLimit(study.getReliabilityLimit())
                .startDate(study.getStartDate())
                .weeks(study.getWeeks())
                .status(study.getState())
                .build();
        }
    }

    public static StudyDetailsResponse fromResult(StudyDetailsResult<?> studyDetailsResult) {
        if (studyDetailsResult.study().getStudyType() == StudyType.ALGORITHM) {
            return StudyDetailsResponse.builder()
                .details(StudyDetails.fromResult(studyDetailsResult.study()))
                .round(AlgorithmStudyProgressResponse.fromResult(
                    studyDetailsResult.currentStudyProgress()))
                .build();
        }
        return null;
    }

}
