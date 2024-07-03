package com.bombombom.devs.study.service.dto.result;

import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.user.service.dto.UserProfileResult;
import java.time.LocalDate;

public interface StudyResult {

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

    UserProfileResult leader();

    static StudyResult fromEntity(Study study) {
        if (study instanceof AlgorithmStudy algorithmStudy) {
            return AlgorithmStudyResult.fromEntity(algorithmStudy);
        } else if (study instanceof BookStudy bookStudy) {
            return BookStudyResult.fromEntity(bookStudy);
        } else {
            return null;
        }
    }
}
