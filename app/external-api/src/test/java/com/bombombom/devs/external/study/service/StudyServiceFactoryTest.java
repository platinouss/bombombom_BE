package com.bombombom.devs.external.study.service;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.bombombom.devs.external.study.service.factory.StudyServiceFactory;
import com.bombombom.devs.study.model.StudyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StudyServiceFactoryTest {

    @Autowired
    private StudyServiceFactory studyServiceFactory;

    @DisplayName("StudyType으로 해당 스터디 타입에 적합한 StudyService를 찾을 수 있다.")
    @Test
    void find_study_service_by_study_type() {
        /*
        Given
         */
        StudyType algorithmStudyType = StudyType.ALGORITHM;
        StudyType bookStudyType = StudyType.BOOK;

        /*
        When
         */
        StudyProgressService algorithmStudyService = studyServiceFactory.getService(
            algorithmStudyType);
        StudyProgressService bookStudyService = studyServiceFactory.getService(bookStudyType);

        /*
        Then
         */
        assertInstanceOf(AlgorithmStudyService.class, algorithmStudyService);
        assertInstanceOf(BookStudyService.class, bookStudyService);
    }
}
