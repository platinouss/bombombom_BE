package com.bombombom.devs.study.controller.dto.response;

import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import com.bombombom.devs.user.controller.dto.UserProfileResponse;
import java.time.LocalDate;


public interface StudyResponse {

    Long id();

    String name();

    String introduce();

    Integer capacity();

    Integer headCount();

    Integer weeks();

    UserProfileResponse leader();

    LocalDate startDate();

    Integer reliabilityLimit();

    Integer penalty();

    StudyStatus state();

    StudyType studyType();

    static StudyResponse fromResult(StudyResult study) {

        if (study instanceof AlgorithmStudyResult algorithmStudyResult) {
            return AlgorithmStudyResponse.fromResult(algorithmStudyResult);
        } else if (study instanceof BookStudyResult bookStudyResult) {
            return BookStudyResponse.fromResult(bookStudyResult);
        } else {
            return null;
        }
    }
}
