package com.bombombom.devs.study.controller;

import static com.bombombom.devs.study.Constants.MAX_CAPACITY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.config.TestUserDetailsServiceConfig;
import com.bombombom.devs.global.exception.GlobalExceptionHandler;
import com.bombombom.devs.global.web.LoginUserArgumentResolver;
import com.bombombom.devs.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.study.controller.dto.response.AlgorithmStudyResponse;
import com.bombombom.devs.study.controller.dto.response.BookStudyResponse;
import com.bombombom.devs.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.service.StudyService;
import com.bombombom.devs.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.study.service.dto.command.RegisterBookStudyCommand;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = StudyController.class, properties = "spring.main.lazy-initialization=true")
@Import(TestUserDetailsServiceConfig.class)
class StudyControllerTest {

    @Autowired
    private StudyController studyController;

    @MockBean
    private StudyService studyService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(studyController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(), new LoginUserArgumentResolver())
            .build();
    }

    @Test
    @DisplayName("studyList 메소드는 StudyPageResponse룰 반환한다 ")
    void study_list_returns_study_page_response() throws Exception {
        /*
        Given
         */
        List<StudyResult> studyResults = new ArrayList<>();

        StudyResult studyResult =
            AlgorithmStudyResult.builder()
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

        StudyResult studyResult2 =
            BookStudyResult.builder()
                .reliabilityLimit(37)
                .capacity(10)
                .introduce("안녕하세요")
                .startDate(LocalDate.of(2024, 06, 14))
                .name("스터디1")
                .penalty(5000)
                .weeks(5)
                .bookId(1024L)
                .build();

        studyResults.add(studyResult);
        studyResults.add(studyResult2);

        Page<StudyResult> serviceResponse =
            new PageImpl<>(studyResults);

        when(studyService.readStudy(any(Pageable.class))).thenReturn(serviceResponse);

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            get("/api/v1/studies")
                .param("page", "1")
                .param("size", "10")
        );

        /*
        Then
         */
        List<StudyResponse> studyResponses = studyResults.stream().map(StudyResponse::fromResult)
            .toList();
        StudyPageResponse studyPageResponse = StudyPageResponse.builder()
            .pageNumber(0)
            .totalPages(1)
            .totalElements(2L)
            .contents(studyResponses)
            .build();

        String expectedResponse = objectMapper.writeValueAsString(studyPageResponse);
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("스터디 가입 요청의 studyId는 1이상의 값이어야 한다.")
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "testUserDetailsService")
    void join_study_request_study_id_should_be_greater_than_0() throws Exception {
        /*
         Given
         */
        String requestBody = objectMapper.writeValueAsString(
            JoinStudyRequest.builder().studyId(0L).build());

        /*
         When
         */
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/studies/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        /*
        Then
         */
        resultActions.andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("registerAlgorithmStudy 메소드는 AlgorithmStudyResponse를 반환한다")
    void register_algorithm_study_returns_algorithm_study_response()
        throws Exception {
        /*
        Given
         */

        RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
            RegisterAlgorithmStudyRequest.builder()
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .capacity(10)
                .startDate(LocalDate.of(2024, 06, 19))
                .penalty(5000)
                .weeks(5)
                .difficultyBegin(10)
                .difficultyEnd(15)
                .problemCount(5).build();

        AlgorithmStudyResult algorithmStudyResult = AlgorithmStudyResult.builder()
            .id(1L)
            .reliabilityLimit(37)
            .introduce("안녕하세요")
            .name("스터디1")
            .headCount(1)
            .capacity(10)
            .startDate(LocalDate.of(2024, 06, 19))
            .penalty(5000)
            .weeks(5)
            .state(StudyStatus.READY)
            .difficultyDp(10f)
            .difficultyDs(10f)
            .difficultyImpl(10f)
            .difficultyGraph(10f)
            .difficultyGreedy(10f)
            .difficultyMath(10f)
            .difficultyString(10f)
            .difficultyGeometry(10f)
            .difficultyGap(5)
            .problemCount(5).build();

        when(studyService.createAlgorithmStudy(
            any(RegisterAlgorithmStudyCommand.class))).thenReturn(algorithmStudyResult);
        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/studies/algo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
        );


        /*
        Then
         */
        StudyResponse studyResponse = StudyResponse.fromResult(
            algorithmStudyResult);
        String expectedResponse = objectMapper.writeValueAsString(studyResponse);

        resultActions.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(expectedResponse));

    }

    @Test
    @DisplayName("registerBookStudy 메소드는 BookStudyResponse를 반환한다")
    void register_book_study_returns_book_study_response() throws Exception {
        /*
        Given
         */
        RegisterBookStudyRequest registerBookStudyRequest =
            RegisterBookStudyRequest.builder()
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .capacity(10)
                .startDate(LocalDate.of(2024, 06, 19))
                .penalty(5000)
                .weeks(5)
                .bookId(15L)
                .build();

        BookStudyResult bookStudyResult =
            BookStudyResult.builder()
                .id(1L)
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .headCount(1)
                .capacity(10)
                .startDate(LocalDate.of(2024, 06, 19))
                .penalty(5000)
                .weeks(5)
                .state(StudyStatus.READY)
                .bookId(15L)
                .build();

        when(studyService.createBookStudy(
            any(RegisterBookStudyCommand.class))).thenReturn(bookStudyResult);

        /*
        When
         */

        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/studies/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerBookStudyRequest))
        );

        /*
        Then
         */
        StudyResponse studyResponse = StudyResponse.fromResult(
            bookStudyResult);
        String expectedResponse = objectMapper.writeValueAsString(studyResponse);

        resultActions.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(expectedResponse));
    }


    @Test
    @DisplayName("BookStudy의 capacity가 1이상 " + MAX_CAPACITY + "이하가 아니라면 생성할 수 없다")
    void register_book_study_fails_if_capcity_not_in_range() throws Exception {
        /*
        Given
         */
        RegisterBookStudyRequest registerBookStudyRequest =
            RegisterBookStudyRequest.builder()
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .capacity(MAX_CAPACITY + 1)
                .startDate(LocalDate.of(2024, 06, 19))
                .penalty(5000)
                .weeks(5)
                .bookId(15L)
                .build();

        /*
        When
         */

        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/studies/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerBookStudyRequest))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isBadRequest());
        verify(studyService, never()).createBookStudy(any(RegisterBookStudyCommand.class));
    }

    @Test
    @DisplayName("AlgorithmStudy의 capacity가 1이상 " + MAX_CAPACITY + "이하가 아니라면 생성할 수 없다")
    void register_algorithm_study_fails_if_capcity_not_in_range() throws Exception {

    }
}