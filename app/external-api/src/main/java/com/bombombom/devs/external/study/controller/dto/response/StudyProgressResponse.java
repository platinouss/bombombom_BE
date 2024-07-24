package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;

public interface StudyProgressResponse {

    static StudyProgressResponse fromResult(StudyProgressResult studyProgress) {
        return switch (studyProgress.studyType()) {
          case ALGORITHM -> AlgorithmStudyProgressResponse.fromResult(studyProgress);
          case BOOK -> BookStudyProgressResponse.fromResult(studyProgress);
        };
    }
}
