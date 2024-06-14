package com.bombombom.devs.study.controller.dto.response;

import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import java.time.LocalDate;
import lombok.Builder;


public interface StudyResponse {

    Long id();

    String name();

    String introduce();

    Integer capacity();

    Integer headCount();

    Integer weeks();

    LocalDate startDate();

    Integer reliabilityLimit();

    Integer penalty();

    StudyStatus state();

    StudyType studyType();

    static StudyResponse of(StudyResult study) {

        if (study instanceof AlgorithmStudyResult algorithmStudyResult) {
            return AlgorithmStudyResponse.of(algorithmStudyResult);
        } else if (study instanceof BookStudyResult bookStudyResult) {
            return BookStudyResponse.of(bookStudyResult);
        } else {
            return null;
        }
    }
}
