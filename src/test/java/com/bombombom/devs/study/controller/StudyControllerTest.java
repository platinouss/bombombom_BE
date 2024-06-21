package com.bombombom.devs.study.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.config.TestUserDetailsServiceConfig;
import com.bombombom.devs.global.exception.GlobalExceptionHandler;
import com.bombombom.devs.global.web.LoginUserArgumentResolver;
import com.bombombom.devs.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.study.controller.dto.response.AlgorithmStudyResponse;
import com.bombombom.devs.study.controller.dto.response.BookStudyResponse;
import com.bombombom.devs.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.service.StudyService;
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
    @DisplayName("스터디 컨트롤러의 studyList 메소드는 StudyPageResponse룰 반환한다 ")
    void study_controller_study_list_return_study_page_response() throws Exception {

        /*
        Given
         */
        List<StudyResponse> serviceResponse = new ArrayList<>();

        StudyResponse studyResponse1 =
            AlgorithmStudyResponse.builder()
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

        StudyResponse studyResponse2 =
            BookStudyResponse.builder()
                .reliabilityLimit(37)
                .capacity(10)
                .introduce("안녕하세요")
                .startDate(LocalDate.of(2024, 06, 14))
                .name("스터디1")
                .penalty(5000)
                .weeks(5)
                .bookId(1024L)
                .build();

        serviceResponse.add(studyResponse1);
        serviceResponse.add(studyResponse2);

        StudyPageResponse studyPageResponse =
            StudyPageResponse.builder()
                .totalElements(2L)
                .totalPages(1)
                .pageNumber(0)
                .contents(serviceResponse)
                .build();
        when(studyService.readStudy(any(Pageable.class))).thenReturn(studyPageResponse);

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
}