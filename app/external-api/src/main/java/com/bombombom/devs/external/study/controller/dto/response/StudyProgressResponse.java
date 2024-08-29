package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import java.time.LocalDate;

public interface StudyProgressResponse {

    Integer idx();

    LocalDate startDate();

    LocalDate endDate();

    static StudyProgressResponse fromResult(StudyProgressResult studyProgress) {
        return switch (studyProgress.studyType()) {
            case ALGORITHM -> AlgorithmStudyProgressResponse.fromResult(studyProgress);
            case BOOK -> BookStudyProgressResponse.fromResult(studyProgress);
        };
    }
}
