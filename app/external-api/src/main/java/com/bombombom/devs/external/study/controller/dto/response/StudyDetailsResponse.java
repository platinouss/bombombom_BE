package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
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
        Long leaderId,
        StudyStatus status
    ) {

        public static StudyDetails fromResult(StudyDetailsResult result) {
            return StudyDetails.builder()
                .studyType(result.studyType())
                .name(result.name())
                .introduce(result.introduce())
                .headCount(result.headCount())
                .capacity(result.capacity())
                .penalty(result.penalty())
                .reliabilityLimit(result.reliabilityLimit())
                .startDate(result.startDate())
                .weeks(result.weeks())
                .leaderId(result.leaderId())
                .status(result.status())
                .build();
        }
    }

    public static StudyDetailsResponse fromResult(StudyDetailsResult studyDetailsResult) {
        return StudyDetailsResponse.builder()
            .details(StudyDetails.fromResult(studyDetailsResult))
            .round(StudyProgressResponse.fromResult(studyDetailsResult.currentStudyProgress()))
            .build();
    }
}
