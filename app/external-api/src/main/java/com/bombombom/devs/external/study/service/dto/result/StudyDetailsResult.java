package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.study.model.Study;
import lombok.Builder;

@Builder
public record StudyDetailsResult<T>(
    Study study,
    StudyProgressResult<T> currentStudyProgress
) {

    public static <T> StudyDetailsResult<T> fromResult(Study study,
        StudyProgressResult<T> currentStudyProgress) {
        return StudyDetailsResult.<T>builder()
            .study(study)
            .currentStudyProgress(currentStudyProgress)
            .build();
    }
}
