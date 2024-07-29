package com.bombombom.devs.external.study.controller;

import static com.bombombom.devs.study.model.Study.MAX_CAPACITY;
import static com.bombombom.devs.study.model.Study.MAX_DIFFICULTY_LEVEL;
import static com.bombombom.devs.study.model.Study.MAX_PENALTY;
import static com.bombombom.devs.study.model.Study.MAX_PROBLEM_COUNT;
import static com.bombombom.devs.study.model.Study.MAX_RELIABILITY_LIMIT;
import static com.bombombom.devs.study.model.Study.MAX_WEEKS;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
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

import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.util.SystemClock;
import com.bombombom.devs.external.book.service.dto.SearchBooksResult.BookResult;
import com.bombombom.devs.external.config.TestUserDetailsServiceConfig;
import com.bombombom.devs.external.global.security.JwtUtils;
import com.bombombom.devs.external.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.external.study.controller.dto.response.AlgorithmStudyProgressResponse;
import com.bombombom.devs.external.study.controller.dto.response.AlgorithmStudyProgressResponse.AlgorithmProblemInfo;
import com.bombombom.devs.external.study.controller.dto.response.AlgorithmStudyProgressResponse.MemberInfo;
import com.bombombom.devs.external.study.controller.dto.response.StudyDetailsResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyDetailsResponse.StudyDetails;
import com.bombombom.devs.external.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.external.study.service.StudyService;
import com.bombombom.devs.external.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.RegisterBookStudyCommand;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmProblemResult;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmProblemSolveHistoryResult;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.external.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.external.study.service.dto.result.RoundResult;
import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.StudyResult;
import com.bombombom.devs.external.study.service.dto.result.progress.AlgorithmStudyProgress;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.study.model.StudyStatus;
import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.user.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@WebMvcTest(controllers = StudyController.class)
@Import({TestUserDetailsServiceConfig.class, JwtUtils.class, SystemClock.class})
class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudyService studyService;

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

            UserProfileResult leaderProfile = UserProfileResult.builder()
                .username("leader")
                .role(Role.USER)
                .introduce("introduce")
                .image("image")
                .reliability(50)
                .money(10000)
                .build();
            BookResult bookResult = BookResult.builder()
                .title("누가 내머리에 똥쌌어")
                .id(5L)
                .isbn(12345689L)
                .publisher("교보문고")
                .author("세계최강민석")
                .tableOfContents("").build();

            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(5000)
                    .weeks(5)
                    .isbn(15L)
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
                    .leader(leaderProfile)
                    .bookResult(bookResult)
                    .weeks(5)
                    .state(StudyStatus.READY)
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
                    .isbn(15L)
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

            resultActions.andExpectAll(
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
                    .isbn(15L)
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

            resultActions.andExpectAll(
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
                    .isbn(15L)
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

            resultActions.andExpectAll(
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
                    .isbn(15L)
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

            resultActions.andExpectAll(
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
                    .isbn(15L)
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

            resultActions.andExpectAll(
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
                    .isbn(15L)
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

            resultActions.andExpectAll(
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
                    .isbn(15L)
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

            resultActions.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.errorDetails.*", hasSize(1)),
                jsonPath("$.errorDetails.reliabilityLimit").hasJsonPath()
            );

            verify(studyService, never()).createBookStudy(any(Long.class),
                any(RegisterBookStudyCommand.class));
        }

        @Test
        @WithUserDetails(value = "testuser")
        @DisplayName("isbn이 Null이라면 BookStudy를 생성할 수 없다")
        void register_book_study_fails_if_isbn_is_null() throws Exception {
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
                    //.bookId(15L)
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

            resultActions.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.errorDetails.*", hasSize(1)),
                jsonPath("$.errorDetails.isbn").hasJsonPath()
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
                    .isbn(15L)
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

            resultActions.andExpectAll(
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
                    .isbn(15L)
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

            resultActions.andExpectAll(
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

            UserProfileResult leaderProfile = UserProfileResult.builder()
                .username("leader")
                .role(Role.USER)
                .introduce("introduce")
                .image("image")
                .reliability(50)
                .money(10000)
                .build();
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
                .leader(leaderProfile)
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

            resultActions.andExpectAll(
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

        UserProfileResult leaderProfile = UserProfileResult.builder()
            .username("leader")
            .role(Role.USER)
            .introduce("introduce")
            .image("image")
            .reliability(50)
            .money(10000)
            .build();
        BookResult bookResult = BookResult.builder()
            .title("누가 내머리에 똥쌌어")
            .id(5L)
            .isbn(12345689L)
            .publisher("교보문고")
            .author("세계최강민석")
            .tableOfContents("").build();

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
                .leader(leaderProfile)
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
                .leader(leaderProfile)
                .startDate(LocalDate.of(2024, 06, 14))
                .name("스터디1")
                .penalty(5000)
                .bookResult(bookResult)
                .weeks(5)
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

    @DisplayName("알고리즘 스터디 진행 현황 조회 테스트")
    @Nested
    class RetrieveAlgorithmStudyProgressTest {

        @DisplayName("특정 회차의 알고리즘 스터디 진행 현황을 조회할 수 있다.")
        @WithMockUser
        @Test
        void can_retrieve_algorithm_study_progress() throws Exception {
            /*
            Given
             */
            Long studyId = 1L;
            Integer roundIdx = 1;
            UserProfileResult user1 = UserProfileResult.builder()
                .id(1L)
                .username("username1")
                .role(Role.USER)
                .reliability(50)
                .build();
            UserProfileResult user2 = UserProfileResult.builder()
                .id(2L)
                .username("username2")
                .role(Role.USER)
                .reliability(60)
                .build();
            RoundResult round = RoundResult.builder()
                .idx(roundIdx)
                .startDate(LocalDate.of(2024, 7, 22))
                .endDate(LocalDate.of(2024, 7, 28))
                .build();
            AlgorithmProblemResult problem1 = AlgorithmProblemResult.builder()
                .id(1L)
                .refId(1000)
                .tag(AlgoTag.DP)
                .title("A")
                .link("https://www.test.com/1000")
                .difficulty(10)
                .build();
            AlgorithmProblemResult problem2 = AlgorithmProblemResult.builder()
                .id(2L)
                .refId(2000)
                .tag(AlgoTag.DP)
                .title("B")
                .link("https://www.test.com/2000")
                .difficulty(5)
                .build();
            AlgorithmProblemSolveHistoryResult history = AlgorithmProblemSolveHistoryResult.builder()
                .problemId(problem2.id())
                .userId(user1.id())
                .solvedAt(LocalDateTime.of(2024, 7, 23, 11, 0))
                .tryCount(2)
                .build();
            AlgorithmStudyProgress algorithmStudyProgress = AlgorithmStudyProgress.builder()
                .round(round)
                .algorithmProblems(List.of(problem1, problem2))
                .histories(List.of(history))
                .build();
            StudyProgressResult studyProgressResult = StudyProgressResult.builder()
                .studyType(StudyType.ALGORITHM)
                .members(List.of(user1, user2))
                .studyProgress(algorithmStudyProgress)
                .build();

            Map<Long, AlgorithmProblemInfo> problems = new HashMap<>();
            AlgorithmProblemInfo algorithmProblemInfo1 = AlgorithmProblemInfo.builder()
                .refId(1000)
                .tag(AlgoTag.DP)
                .title("A")
                .link("https://www.test.com/1000")
                .difficulty(10)
                .build();
            AlgorithmProblemInfo algorithmProblemInfo2 = AlgorithmProblemInfo.builder()
                .refId(2000)
                .tag(AlgoTag.DP)
                .title("B")
                .link("https://www.test.com/2000")
                .difficulty(5)
                .build();
            problems.put(1L, algorithmProblemInfo1);
            problems.put(2L, algorithmProblemInfo2);
            Map<Long, MemberInfo> users = new HashMap<>();
            Map<Long, Boolean> tasks1 = new HashMap<>();
            tasks1.put(1L, false);
            tasks1.put(2L, true);
            MemberInfo memberInfo1 = MemberInfo.builder().username("username1").tasks(tasks1)
                .build();
            Map<Long, Boolean> tasks2 = new HashMap<>();
            tasks2.put(1L, false);
            tasks2.put(2L, false);
            MemberInfo memberInfo2 = MemberInfo.builder().username("username2").tasks(tasks2)
                .build();
            users.put(1L, memberInfo1);
            users.put(2L, memberInfo2);
            AlgorithmStudyProgressResponse progressResponse = AlgorithmStudyProgressResponse.builder()
                .idx(roundIdx)
                .startDate(LocalDate.of(2024, 7, 22))
                .endDate(LocalDate.of(2024, 7, 28))
                .problems(problems)
                .users(users)
                .build();

            doReturn(studyProgressResult).when(studyService).findStudyProgress(anyLong(), anyInt());

            /*
            When
             */
            ResultActions resultActions = mockMvc.perform(
                get("/api/v1/studies/progress/" + studyId).param("idx", String.valueOf(roundIdx))
            );

            /*
            Then
             */
            String expectedResponse = objectMapper.writeValueAsString(progressResponse);
            resultActions.andExpect(status().isOk()).andExpect(content().json(expectedResponse));
        }

        @DisplayName("studyId가 숫자 타입이 아닌 경우 알고리즘 스터디 진행 현황 조회에 실패한다.")
        @WithMockUser
        @Test
        void retrieve_algorithm_study_progress_for_invalid_study_id_fail() throws Exception {
            /*
            Given
             */
            String studyId = "test";
            Integer roundIdx = 1;
            UserProfileResult user1 = UserProfileResult.builder()
                .id(1L)
                .username("username1")
                .role(Role.USER)
                .reliability(50)
                .build();
            UserProfileResult user2 = UserProfileResult.builder()
                .id(2L)
                .username("username2")
                .role(Role.USER)
                .reliability(60)
                .build();
            RoundResult round = RoundResult.builder()
                .idx(1)
                .startDate(LocalDate.of(2024, 7, 22))
                .endDate(LocalDate.of(2024, 7, 28))
                .build();
            AlgorithmProblemResult problem1 = AlgorithmProblemResult.builder()
                .id(1L)
                .refId(1000)
                .tag(AlgoTag.DP)
                .title("A")
                .link("https://www.test.com/1000")
                .difficulty(10)
                .build();
            AlgorithmProblemResult problem2 = AlgorithmProblemResult.builder()
                .id(2L)
                .refId(2000)
                .tag(AlgoTag.DP)
                .title("B")
                .link("https://www.test.com/2000")
                .difficulty(5)
                .build();
            AlgorithmProblemSolveHistoryResult history = AlgorithmProblemSolveHistoryResult.builder()
                .problemId(problem2.id())
                .userId(user1.id())
                .solvedAt(LocalDateTime.of(2024, 7, 23, 11, 0))
                .tryCount(2)
                .build();
            AlgorithmStudyProgress algorithmStudyProgress = AlgorithmStudyProgress.builder()
                .round(round)
                .algorithmProblems(List.of(problem1, problem2))
                .histories(List.of(history))
                .build();
            StudyProgressResult studyProgressResult = StudyProgressResult.builder()
                .studyType(StudyType.ALGORITHM)
                .members(List.of(user1, user2))
                .studyProgress(algorithmStudyProgress)
                .build();

            doReturn(studyProgressResult).when(studyService).findStudyProgress(anyLong(), anyInt());

            /*
            When
             */
            ResultActions resultActions = mockMvc.perform(
                get("/api/v1/studies/progress/" + studyId).param("idx", String.valueOf(roundIdx))
            );

            /*
            Then
             */
            resultActions.andExpect(status().isBadRequest());
        }

        @DisplayName("round의 idx가 숫자 타입이 아닌 경우 알고리즘 스터디 진행 현황 조회에 실패한다.")
        @WithMockUser
        @Test
        void retrieve_algorithm_study_progress_for_invalid_round_idx_fail() throws Exception {
            /*
            Given
             */
            Long studyId = 1L;
            String roundIdx = "test";
            UserProfileResult user1 = UserProfileResult.builder()
                .id(1L)
                .username("username1")
                .role(Role.USER)
                .reliability(50)
                .build();
            UserProfileResult user2 = UserProfileResult.builder()
                .id(2L)
                .username("username2")
                .role(Role.USER)
                .reliability(60)
                .build();
            RoundResult round = RoundResult.builder()
                .idx(1)
                .startDate(LocalDate.of(2024, 7, 22))
                .endDate(LocalDate.of(2024, 7, 28))
                .build();
            AlgorithmProblemResult problem1 = AlgorithmProblemResult.builder()
                .id(1L)
                .refId(1000)
                .tag(AlgoTag.DP)
                .title("A")
                .link("https://www.test.com/1000")
                .difficulty(10)
                .build();
            AlgorithmProblemResult problem2 = AlgorithmProblemResult.builder()
                .id(2L)
                .refId(2000)
                .tag(AlgoTag.DP)
                .title("B")
                .link("https://www.test.com/2000")
                .difficulty(5)
                .build();
            AlgorithmProblemSolveHistoryResult history = AlgorithmProblemSolveHistoryResult.builder()
                .problemId(problem2.id())
                .userId(user1.id())
                .solvedAt(LocalDateTime.of(2024, 7, 23, 11, 0))
                .tryCount(2)
                .build();
            AlgorithmStudyProgress algorithmStudyProgress = AlgorithmStudyProgress.builder()
                .round(round)
                .algorithmProblems(List.of(problem1, problem2))
                .histories(List.of(history))
                .build();
            StudyProgressResult studyProgressResult = StudyProgressResult.builder()
                .studyType(StudyType.ALGORITHM)
                .members(List.of(user1, user2))
                .studyProgress(algorithmStudyProgress)
                .build();

            doReturn(studyProgressResult).when(studyService).findStudyProgress(anyLong(), anyInt());

            /*
            When
             */
            ResultActions resultActions = mockMvc.perform(
                get("/api/v1/studies/progress/" + studyId).param("idx", roundIdx)
            );

            /*
            Then
             */
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @DisplayName("알고리즘 스터디 정보 조회 테스트")
    @Nested
    class RetrieveAlgorithmStudyDetailsTest {

        @DisplayName("알고리즘 스터디 정보를 조회할 수 있다.")
        @WithMockUser
        @Test
        void can_retrieve_algorithm_study_details() throws Exception {
            /*
            Given
             */
            Long studyId = 1L;
            Integer roundIdx = 1;
            UserProfileResult user1 = UserProfileResult.builder()
                .id(1L)
                .username("username1")
                .role(Role.USER)
                .reliability(50)
                .build();
            UserProfileResult user2 = UserProfileResult.builder()
                .id(2L)
                .username("username2")
                .role(Role.USER)
                .reliability(60)
                .build();
            RoundResult round = RoundResult.builder()
                .idx(roundIdx)
                .startDate(LocalDate.of(2024, 7, 22))
                .endDate(LocalDate.of(2024, 7, 28))
                .build();
            AlgorithmProblemResult problem1 = AlgorithmProblemResult.builder()
                .id(1L)
                .refId(1000)
                .tag(AlgoTag.DP)
                .title("A")
                .link("https://www.test.com/1000")
                .difficulty(10)
                .build();
            AlgorithmProblemResult problem2 = AlgorithmProblemResult.builder()
                .id(2L)
                .refId(2000)
                .tag(AlgoTag.DP)
                .title("B")
                .link("https://www.test.com/2000")
                .difficulty(5)
                .build();
            AlgorithmProblemSolveHistoryResult history = AlgorithmProblemSolveHistoryResult.builder()
                .problemId(problem2.id())
                .userId(user1.id())
                .solvedAt(LocalDateTime.of(2024, 7, 23, 11, 0))
                .tryCount(2)
                .build();
            AlgorithmStudyProgress algorithmStudyProgress = AlgorithmStudyProgress.builder()
                .round(round)
                .algorithmProblems(List.of(problem1, problem2))
                .histories(List.of(history))
                .build();
            StudyProgressResult studyProgressResult = StudyProgressResult.builder()
                .studyType(StudyType.ALGORITHM)
                .members(List.of(user1, user2))
                .studyProgress(algorithmStudyProgress)
                .build();
            StudyDetailsResult studyDetailsResult = StudyDetailsResult.builder()
                .studyType(StudyType.ALGORITHM)
                .name("스터디")
                .introduce("안녕하세요")
                .headCount(1)
                .capacity(5)
                .penalty(10000)
                .reliabilityLimit(0)
                .startDate(LocalDate.of(2024, 7, 22))
                .weeks(2)
                .leaderId(1L)
                .status(StudyStatus.RUNNING)
                .currentStudyProgress(studyProgressResult)
                .build();

            StudyDetails studyDetails = StudyDetails.builder()
                .studyType(StudyType.ALGORITHM)
                .name("스터디")
                .introduce("안녕하세요")
                .headCount(1)
                .capacity(5)
                .penalty(10000)
                .reliabilityLimit(0)
                .startDate(LocalDate.of(2024, 7, 22))
                .weeks(2)
                .leaderId(1L)
                .status(StudyStatus.RUNNING)
                .build();
            Map<Long, AlgorithmProblemInfo> problems = new HashMap<>();
            AlgorithmProblemInfo algorithmProblemInfo1 = AlgorithmProblemInfo.builder()
                .refId(1000)
                .tag(AlgoTag.DP)
                .title("A")
                .link("https://www.test.com/1000")
                .difficulty(10)
                .build();
            AlgorithmProblemInfo algorithmProblemInfo2 = AlgorithmProblemInfo.builder()
                .refId(2000)
                .tag(AlgoTag.DP)
                .title("B")
                .link("https://www.test.com/2000")
                .difficulty(5)
                .build();
            problems.put(1L, algorithmProblemInfo1);
            problems.put(2L, algorithmProblemInfo2);
            Map<Long, MemberInfo> users = new HashMap<>();
            Map<Long, Boolean> tasks1 = new HashMap<>();
            tasks1.put(1L, false);
            tasks1.put(2L, true);
            MemberInfo memberInfo1 = MemberInfo.builder().username("username1").tasks(tasks1)
                .build();
            Map<Long, Boolean> tasks2 = new HashMap<>();
            tasks2.put(1L, false);
            tasks2.put(2L, false);
            MemberInfo memberInfo2 = MemberInfo.builder().username("username2").tasks(tasks2)
                .build();
            users.put(1L, memberInfo1);
            users.put(2L, memberInfo2);
            AlgorithmStudyProgressResponse progressResponse = AlgorithmStudyProgressResponse.builder()
                .idx(roundIdx)
                .startDate(LocalDate.of(2024, 7, 22))
                .endDate(LocalDate.of(2024, 7, 28))
                .problems(problems)
                .users(users)
                .build();
            StudyDetailsResponse studyDetailsResponse = StudyDetailsResponse.builder()
                .details(studyDetails)
                .round(progressResponse)
                .build();

            doReturn(studyDetailsResult).when(studyService).findStudyDetails(anyLong());

            /*
            When
             */
            ResultActions resultActions = mockMvc.perform(get("/api/v1/studies/" + studyId));

            /*
            Then
             */
            String expectedResponse = objectMapper.writeValueAsString(studyDetailsResponse);
            resultActions.andExpect(status().isOk()).andExpect(content().json(expectedResponse));

        }

        @DisplayName("studyId가 숫자 타입이 아닌 경우 알고리즘 스터디 상세 정보 조회에 실패한다.")
        @WithMockUser
        @Test
        void retrieve_algorithm_study_details_for_invalid_study_id_fail() throws Exception {
            /*
            Given
             */
            String studyId = "test";
            Integer roundIdx = 1;
            UserProfileResult user1 = UserProfileResult.builder()
                .id(1L)
                .username("username1")
                .role(Role.USER)
                .reliability(50)
                .build();
            UserProfileResult user2 = UserProfileResult.builder()
                .id(2L)
                .username("username2")
                .role(Role.USER)
                .reliability(60)
                .build();
            RoundResult round = RoundResult.builder()
                .idx(roundIdx)
                .startDate(LocalDate.of(2024, 7, 22))
                .endDate(LocalDate.of(2024, 7, 28))
                .build();
            AlgorithmProblemResult problem1 = AlgorithmProblemResult.builder()
                .id(1L)
                .refId(1000)
                .tag(AlgoTag.DP)
                .title("A")
                .link("https://www.test.com/1000")
                .difficulty(10)
                .build();
            AlgorithmProblemResult problem2 = AlgorithmProblemResult.builder()
                .id(2L)
                .refId(2000)
                .tag(AlgoTag.DP)
                .title("B")
                .link("https://www.test.com/2000")
                .difficulty(5)
                .build();
            AlgorithmProblemSolveHistoryResult history = AlgorithmProblemSolveHistoryResult.builder()
                .problemId(problem2.id())
                .userId(user1.id())
                .solvedAt(LocalDateTime.of(2024, 7, 23, 11, 0))
                .tryCount(2)
                .build();
            AlgorithmStudyProgress algorithmStudyProgress = AlgorithmStudyProgress.builder()
                .round(round)
                .algorithmProblems(List.of(problem1, problem2))
                .histories(List.of(history))
                .build();
            StudyProgressResult studyProgressResult = StudyProgressResult.builder()
                .studyType(StudyType.ALGORITHM)
                .members(List.of(user1, user2))
                .studyProgress(algorithmStudyProgress)
                .build();
            StudyDetailsResult studyDetailsResult = StudyDetailsResult.builder()
                .studyType(StudyType.ALGORITHM)
                .name("스터디")
                .introduce("안녕하세요")
                .headCount(1)
                .capacity(5)
                .penalty(10000)
                .reliabilityLimit(0)
                .startDate(LocalDate.of(2024, 7, 22))
                .weeks(2)
                .leaderId(1L)
                .status(StudyStatus.RUNNING)
                .currentStudyProgress(studyProgressResult)
                .build();

            doReturn(studyDetailsResult).when(studyService).findStudyDetails(anyLong());

            /*
            When
             */
            ResultActions resultActions = mockMvc.perform(get("/api/v1/studies/" + studyId));

            /*
            Then
             */
            resultActions.andExpect(status().isBadRequest());
        }
    }
}