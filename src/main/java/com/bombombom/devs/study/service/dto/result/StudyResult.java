package com.bombombom.devs.study.service.dto.result;

import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import java.time.LocalDate;

public interface StudyResult {
    Long id();
    String name();
    String introduce();
    int capacity();
    int headCount();
    int weeks();
    LocalDate startDate();
    int reliabilityLimit();
    int penalty();
    StudyStatus state();
    StudyType studyType();
}
