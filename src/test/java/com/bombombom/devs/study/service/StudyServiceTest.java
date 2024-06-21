package com.bombombom.devs.study.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bombombom.devs.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.study.service.dto.command.JoinStudyCommand;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import com.bombombom.devs.user.models.User;
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

    @Mock
    private UserStudyRepository userStudyRepository;

    @InjectMocks
    private StudyService studyService;

    @Test
    @DisplayName("스터디 서비스의 readStudy 메소드는 StudyPageResponse를 반환한다")
    void study_service_read_study_returns_study_page_response() throws Exception {
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
        StudyPageResponse studyPageResponse = studyService.readStudy(PageRequest.of(0, 10));

        /*
        Then
         */
        List<StudyResponse> studyList = repositoryResponses.stream()
            .map(StudyResult::fromEntity).map(StudyResponse::of).toList();
        StudyPageResponse expectedResponse = StudyPageResponse.builder()
            .contents(studyList)
            .pageNumber(0)
            .totalPages(1)
            .totalElements(2L)
            .build();
        Assertions.assertThat(studyPageResponse).isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("유저는 이미 가입한 스터디에 다시 가입할 수 없다.")
    void user_cannot_join_study_twice() {
        /*
         * Given
         */
        User testuser = User.builder()
            .id(1L)
            .username("testuser")
            .money(100000)
            .reliability(10)
            .build();
        Study study = AlgorithmStudy.builder()
            .capacity(10)
            .headCount(1)
            .weeks(10)
            .reliabilityLimit(10)
            .penalty(1000)
            .state(StudyStatus.READY)
            .build();
        JoinStudyCommand joinStudyCommand = JoinStudyCommand.builder().studyId(study.getId()).build();
        when(userStudyRepository.existsByUserIdAndStudyId(testuser.getId(), study.getId()))
            .thenReturn(true);

        /*
         * When & Then
         */
        assertThatThrownBy(() -> studyService.joinStudy(
            testuser.getId(), joinStudyCommand))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Already Joined Study");
    }

}