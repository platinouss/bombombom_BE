package com.bombombom.devs.external.study.service.dto.result;

import lombok.Builder;

@Builder
public record StudyDetailsResult(
    StudyResult studyResult,
    StudyProgressResult currentStudyProgress
) {

}
