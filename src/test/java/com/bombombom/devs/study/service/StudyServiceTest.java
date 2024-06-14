package com.bombombom.devs.study.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @InjectMocks
    private StudyService studyService;

    @Test
    @DisplayName("스터디 서비스의 readStudy 메소드는 StudyResponse 리스트를 반환한다")
    void study_service_read_study_returns_list_of_study_response() throws Exception {
        /*
        Given
         */
        List<Study> repositoryResponses = new ArrayList<>();

        Study study1 =
            AlgorithmStudy.builder()
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .startDate(LocalDate.of(2024, 06, 14))
                .penalty(5000)
                .weeks(5)
                .difficultyDp(12.4f)
                .difficultyDs(12f)
                .difficultyGraph(12.9f)
                .difficultyGap(5)
                .capacity(10)
                .difficultyGeometry(11f)
                .difficultyMath(11f)
                .difficultyString(13.5f)
                .problemCount(5)
                .build();

        Study study2 =
            BookStudy.builder()
                .reliabilityLimit(37)
                .capacity(10)
                .introduce("안녕하세요")
                .startDate(LocalDate.of(2024, 06, 14))
                .name("스터디1")
                .penalty(5000)
                .weeks(5)
                .bookId(1024L)
                .build();

        repositoryResponses.add(study1);
        repositoryResponses.add(study2);

        Page<Study> studies = new PageImpl<>(repositoryResponses);
        when(studyRepository.findAll(any(Pageable.class))).thenReturn(studies);

        /*
        When
         */
        List<StudyResponse> responses = studyService.readStudy(PageRequest.of(1, 10));

        /*
        Then
         */
        List<StudyResponse> expectedResponse = repositoryResponses.stream()
            .map(StudyResult::fromEntity).map(StudyResponse::of).toList();

        Assertions.assertThat(responses).isEqualTo(expectedResponse);

    }


}