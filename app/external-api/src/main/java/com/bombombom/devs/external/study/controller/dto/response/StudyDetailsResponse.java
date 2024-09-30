package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
import lombok.Builder;

@Builder
public record StudyDetailsResponse(
    StudyResponse details,
    StudyProgressResponse round
) {


    public static StudyDetailsResponse fromResult(StudyDetailsResult studyDetailsResult) {
        return StudyDetailsResponse.builder()
            .details(StudyResponse.fromResult(studyDetailsResult.studyResult()))
            .round(StudyProgressResponse.fromResult(studyDetailsResult.currentStudyProgress()))
            .build();
    }
}
