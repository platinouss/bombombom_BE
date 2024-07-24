package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.study.model.Study;
import lombok.Builder;

@Builder
public record StudyDetailsResult(
    Study study,
    StudyProgressResult currentStudyProgress
) {

    public static StudyDetailsResult fromResult(Study study,
        StudyProgressResult currentStudyProgress) {
        return StudyDetailsResult.builder()
            .study(study)
            .currentStudyProgress(currentStudyProgress)
            .build();
    }
}
