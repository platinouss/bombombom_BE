package com.bombombom.devs.external.study;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.ExternalApiApplication;
import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.external.book.service.dto.SearchBooksResult;
import com.bombombom.devs.external.config.ElasticsearchTestConfig;
import com.bombombom.devs.external.study.controller.StudyController;
import com.bombombom.devs.external.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.external.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.external.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.external.study.service.dto.result.StudyResult;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.BookStudy;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.StudyStatus;
import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.user.model.Role;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = ExternalApiApplication.class)
@Import(ElasticsearchTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class StudyIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudyController studyController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Nested
    @DisplayName("인증이 필요한 테스트")
    @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    class TestWithAuthentication {

        private User testuser;

        @BeforeEach
        public void init() {
            testuser = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .introduce("introduce")
                .image("image")
                .reliability(50)
                .money(10000)
                .build();

            userRepository.save(testuser);

        }

        @Test
        @DisplayName("스터디 입장 조건에 맞는 유저는 입장할 수 있다.")
        @WithUserDetails(value = "testuser",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void can_join_study_if_user_meets_the_conditions() throws Exception {
            /*
             Given
             */
            Book book = Book.builder()
                .title("테스트용 책")
                .author("세계최강민석")
                .isbn(123456789L)
                .publisher("메가스터디")
                .tableOfContents("1. 2. 3. 4.")
                .build();
            bookRepository.save(book);

            User leader = User.builder()
                .username("leader")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .introduce("introduce")
                .image("image")
                .reliability(50)
                .money(10000)
                .build();
            userRepository.save(leader);

            Study study =
                BookStudy.builder()
                    .reliabilityLimit(37)
                    .capacity(10)
                    .introduce("안녕하세요")
                    .startDate(LocalDate.now())
                    .name("스터디")
                    .leader(leader)
                    .penalty(1000)
                    .weeks(5)
                    .state(StudyStatus.READY)
                    .headCount(0)
                    .book(book)
                    .build();
            studyRepository.save(study);
            JoinStudyRequest request = JoinStudyRequest.builder()
                .studyId(study.getId()).build();

            System.out.println("study.getId() = " + study.getId());
            /*
             When
             */
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            /*
            Then
             */
            resultActions.andDo(print())
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("알고리즘 스터디를 생성할 수 있다")
        @WithUserDetails(value = "testuser",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void can_register_algorithm_study() throws Exception {
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
                    .penalty(1000)
                    .weeks(5)
                    .difficultyBegin(10)
                    .difficultyEnd(15)
                    .problemCount(5).build();

            UserProfileResult profile = UserProfileResult.builder()
                .id(testuser.getId())
                .role(testuser.getRole())
                .introduce(testuser.getIntroduce())
                .money(testuser.getMoney() - 5000)
                .reliability(testuser.getReliability())
                .username(testuser.getUsername())
                .image(testuser.getImage())
                .build();

            AlgorithmStudyResult algorithmStudyResult = AlgorithmStudyResult.builder()
                .id(1L)
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .headCount(1)
                .capacity(10)
                .startDate(LocalDate.now())
                .penalty(1000)
                .weeks(5)
                .leader(profile)
                .state(StudyStatus.READY)
                .studyType(StudyType.ALGORITHM)
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

            resultActions = mockMvc.perform(
                get("/api/v1/users/me")
            );
            resultActions.andDo(print());
        }


        @Test
        @DisplayName("기술서적 스터디를 생성할 수 있다")
        @WithUserDetails(value = "testuser",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void can_register_book_study() throws Exception {
            /*
            Given
             */
            User leader = User.builder()
                .username("leader")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .introduce("introduce")
                .image("image")
                .reliability(50)
                .money(10000)
                .build();
            userRepository.save(leader);

            Book book = Book.builder()
                .title("테스트용 책")
                .author("세계최강민석")
                .isbn(123456789L)
                .publisher("메가스터디")
                .tableOfContents("1. 2. 3. 4.")
                .build();
            bookRepository.save(book);

            UserProfileResult profile = UserProfileResult.builder()
                .id(testuser.getId())
                .role(testuser.getRole())
                .introduce(testuser.getIntroduce())
                .money(testuser.getMoney() - 5000)
                .reliability(testuser.getReliability())
                .username(testuser.getUsername())
                .image(testuser.getImage())
                .build();

            RegisterBookStudyRequest registerBookStudyRequest =
                RegisterBookStudyRequest.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .capacity(10)
                    .startDate(LocalDate.now())
                    .penalty(1000)
                    .weeks(5)
                    .isbn(123456789L)
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
                    .penalty(1000)
                    .weeks(5)
                    .leader(profile)
                    .state(StudyStatus.READY)
                    .studyType(StudyType.BOOK)
                    .bookResult(SearchBooksResult.fromBook(book))
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
            StudyResponse studyResponse = StudyResponse.fromResult(
                bookStudyResult);
            String expectedResponse = objectMapper.writeValueAsString(studyResponse);

            resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));

        }


        @Test
        @DisplayName("책을 찾지 못한 경우 기술서적 스터디를 생성할 수 없다")
        @WithUserDetails(value = "testuser",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void register_book_study_fails_if_book_is_not_found() throws Exception {
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
                    .penalty(1000)
                    .weeks(5)
                    .isbn(123456789L)
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
            resultActions.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book Not Found"));


        }
    }

    @Test
    @DisplayName("스터디 목록을 offset기반 pagination을 통해 조회할 수 있다")
    void can_retrieve_study_list_through_offset_based_pagination()
        throws Exception {
        /*
        Given
         */

        User leader = User.builder()
            .username("leader")
            .password(passwordEncoder.encode("password"))
            .role(Role.USER)
            .introduce("introduce")
            .image("image")
            .reliability(50)
            .money(10000)
            .build();
        userRepository.save(leader);

        Book book = Book.builder()
            .title("테스트용 책")
            .author("세계최강민석")
            .isbn(123456789L)
            .publisher("메가스터디")
            .tableOfContents("1. 2. 3. 4.")
            .build();
        bookRepository.save(book);
        Study study1 =
            AlgorithmStudy.builder()
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .startDate(LocalDate.of(2024, 06, 14))
                .penalty(5000)
                .weeks(5)
                .state(StudyStatus.READY)
                .headCount(0)
                .leader(leader)
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
                .leader(leader)
                .book(book)
                .state(StudyStatus.READY)
                .headCount(0)
                .build();

        studyRepository.save(study1);
        studyRepository.save(study2);
        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            get("/api/v1/studies")
                .param("page", "1")
                .param("size", "1")
        );


        /*
        Then
         */
        List<StudyResponse> studies = new ArrayList<>();
        studies.add(StudyResponse.fromResult(StudyResult.fromEntity(study1)));

        StudyPageResponse studyPageResponse = StudyPageResponse.builder()
            .totalPages(2)
            .totalElements(2L)
            .pageNumber(1)
            .contents(studies)
            .build();
        String expectedResponse = objectMapper.writeValueAsString(studyPageResponse);
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
    }


}
