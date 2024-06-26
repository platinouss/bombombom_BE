package com.bombombom.devs.study.controller;

import static com.bombombom.devs.study.Constants.MAX_CAPACITY;
import static com.bombombom.devs.study.Constants.MAX_DIFFICULTY_LEVEL;
import static com.bombombom.devs.study.Constants.MAX_PENALTY;
import static com.bombombom.devs.study.Constants.MAX_PROBLEM_COUNT;
import static com.bombombom.devs.study.Constants.MAX_RELIABILITY_LIMIT;
import static com.bombombom.devs.study.Constants.MAX_WEEKS;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.config.TestUserDetailsServiceConfig;
import com.bombombom.devs.global.security.JwtUtils;
import com.bombombom.devs.global.util.SystemClock;
import com.bombombom.devs.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.service.StudyService;
import com.bombombom.devs.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.study.service.dto.command.RegisterBookStudyCommand;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
@WebMvcTest(StudyController.class)
@Import({TestUserDetailsServiceConfig.class, JwtUtils.class, SystemClock.class})
class StudyControllerTest {


    @MockBean
    private StudyService studyService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("BookStudy 생성 테스트")
    @Nested
    class RegisterBookStudyTest {

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("성공 시 BookStudyResponse를 반환한다")
        void register_book_study_returns_book_study_response_if_success() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(10)
                    .startDate(LocalDate.now())
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
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .state(StudyStatus.READY)
                    .bookId(15L)
                    .build();

            when(studyService.createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class))).thenReturn(bookStudyResult);

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
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
        @WithUserDetails(value = "testuser")
        @DisplayName("name이 공백이라면 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_name_is_empty() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name(" ")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .bookId(15L)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBookStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.name").hasJsonPath()
                );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("name이 255자를 넘는다면 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_name_size_exceed_255() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("하이요")
                    .name("a".repeat(256))
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .bookId(15L)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBookStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.name").hasJsonPath()
                );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("introduce가 공백이라면 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_introduce_is_empty() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce(" ")
                    .name("aaa")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .bookId(15L)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBookStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.introduce").hasJsonPath()
                );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("introduce가 500자를 넘는다면 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_introduce_size_exceed_500() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("ㅋ".repeat(501))
                    .name("아령하세요")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .bookId(15L)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBookStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.introduce").hasJsonPath()
                );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("weeks가 1이상 " + MAX_WEEKS + "이하가 아니라면 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_weeks_not_in_range() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(MAX_WEEKS + 1)
                    .bookId(15L)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBookStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.weeks").hasJsonPath()
                );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("penalty가 0이상 " + MAX_PENALTY + "이하가 아니라면 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_penalty_not_in_range() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(30)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(MAX_PENALTY + 1)
                    .weeks(5)
                    .bookId(15L)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBookStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.penalty").hasJsonPath()
                );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "reliabilityLimit가 0이상 " + MAX_RELIABILITY_LIMIT + "이하가 아니라면 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_reliability_limit_not_in_range() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(MAX_RELIABILITY_LIMIT + 1)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .bookId(15L)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBookStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.reliabilityLimit").hasJsonPath()
                );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("bookId가 Null이라면 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_book_id_is_null() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("아니 테스트코드를 이렇게나 많이 짜는게 맞는건가?? ..")
                    .name("아령하세요")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
//                .bookId(15L)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBookStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.bookId").hasJsonPath()
                );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }


        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("startDate가 오늘 이전이라면 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_start_date_is_before_today() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("아니 테스트코드를 이렇게나 많이 짜는게 맞는건가?? ..")
                    .name("아령하세요")
                    .capacity(10)
                    .startDate(LocalDate.now().minusMonths(1))
                    .penalty(5000)
                    .weeks(5)
                    .bookId(15L)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBookStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.startDateAfterOrEqualToday").hasJsonPath()
                );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("인증받지 않은 사용자는 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_not_authenticated() throws Exception {
            /*
            Given
             */
            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("아니 테스트코드를 이렇게나 많이 짜는게 맞는건가?? ..")
                    .name("아령하세요")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .bookId(15L)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/book")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBookStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isUnauthorized()
                );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }
    }

    @DisplayName("AlgorithmStudy 생성 테스트")
    @Nested
    class RegisterAlgorithmStudyTest {

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("성공 시 AlgorithmStudyResponse를 반환한다")
        void register_algorithm_study_returns_algorithm_study_response_if_success()
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
                    .startDate(LocalDate.now())
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
                .startDate(LocalDate.now())
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

            when(studyService.createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class))).thenReturn(algorithmStudyResult);
            /*
            When
             */
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
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
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "name이 공백이라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_name_is_empty() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(50)
                    .introduce("안녕하세요")
                    .name("")
                    .capacity(1)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(50)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.name").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }


        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "name이 255자을 넘는다면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_name_size_exceed_255() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(50)
                    .introduce("when you get order your wild heart will live for younger days")
                    .name("스".repeat(256))
                    .capacity(1)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(50)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.name").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }


        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "introduce가 공백이라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_introduce_is_empty() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(50)
                    .introduce("")
                    .name("봄봄봄봄봄")
                    .capacity(1)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(50)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.introduce").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }


        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "introduce가 500자를 넘는다면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_introduce_size_exceed_500() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(50)
                    .introduce("a".repeat(501))
                    .name("봄봄봄봄봄")
                    .capacity(1)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(50)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.introduce").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "weeks가 1이상 " + MAX_WEEKS + "이하가 아니라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_weeks_not_in_range() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(50)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(1)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(0)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.weeks").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "penalty가 0이상 " + MAX_PENALTY + "이하가 아니라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_penalty_not_in_range() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(1)
                    .startDate(LocalDate.now())
                    .penalty(MAX_PENALTY + 1)
                    .weeks(5)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.penalty").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "capacity가 1이상 " + MAX_CAPACITY + "이하가 아니라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_capacity_not_in_range() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(0)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.capacity").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "reliablityLimit이 0이상 " + MAX_RELIABILITY_LIMIT + "이하가 아니라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_reliabilityLimit_not_in_range() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(MAX_RELIABILITY_LIMIT + 1)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(1)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.reliabilityLimit").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }


        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "difficultyBegin이 0이상 " + MAX_DIFFICULTY_LEVEL + "이하가 아니라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_difficulty_begin_not_in_range() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .difficultyBegin(-1)
                    .difficultyEnd(10)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.difficultyBegin").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }


        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "difficultyEnd가 0이상 " + MAX_DIFFICULTY_LEVEL + "이하가 아니라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_difficulty_end_not_in_range() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .difficultyBegin(10)
                    .difficultyEnd(MAX_DIFFICULTY_LEVEL + 1)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.difficultyEnd").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("problemCount가 1이상 " + MAX_PROBLEM_COUNT + "이하가 아니라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_problem_count_not_in_range() throws Exception {
            /*
            Given
             */
            RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest =
                RegisterAlgorithmStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .difficultyBegin(5)
                    .difficultyEnd(10)
                    .problemCount(0)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.problemCount").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }


        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "difficultyBegin이 difficultyEnd보다 크다면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_difficulty_begin_is_greater_than_difficulty_end()
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
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .difficultyBegin(10)
                    .difficultyEnd(9)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.difficultyBeginLteDifficultyEnd").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName(
            "startDate가 오늘 이전이라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_start_date_is_before_today()
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
                    .startDate(LocalDate.now().minusDays(1))
                    .penalty(5000)
                    .weeks(5)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.errorDetails.*", hasSize(1)),
                    jsonPath("$.errorDetails.startDateAfterOrEqualToday").hasJsonPath()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }

        @Test
        @WithAnonymousUser
        @DisplayName(
            "startDate가 오늘 이전이라면 AlgorithmStudy를 생성할 수 없다")
        void register_algorithm_study_fails_if_not_authenticated()
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
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5)
                    .build();

            /*
            When
             */

            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/algo")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerAlgorithmStudyRequest))
            );

            /*
            Then
             */

            resultActions
                .andExpectAll(
                    status().isUnauthorized()
                );

            verify(studyService, never()).createAlgorithmStudy(any(Long.class),
                any(RegisterAlgorithmStudyCommand.class));
        }
    }

    @Test
    @WithMockUser
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
    @WithUserDetails(value = "testuser")
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
                .with(csrf())
        );

        /*
        Then
         */
        resultActions.andDo(print())
            .andExpect(status().isBadRequest());
    }

}