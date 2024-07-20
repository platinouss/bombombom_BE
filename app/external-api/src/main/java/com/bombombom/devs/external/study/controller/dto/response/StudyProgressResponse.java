package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.study.model.StudyType;

public interface StudyProgressResponse {

    static StudyProgressResponse fromResult(StudyProgressResult<?> studyProgress) {
        if (studyProgress.studyType() == StudyType.ALGORITHM) {
            return AlgorithmStudyProgressResponse.fromResult(studyProgress);
        }
        return null;
    }
}
