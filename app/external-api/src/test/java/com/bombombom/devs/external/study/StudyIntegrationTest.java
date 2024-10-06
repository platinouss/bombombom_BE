package com.bombombom.devs.external.study;


import static com.bombombom.devs.study.model.Study.MAX_DIFFICULTY_LEVEL;
import static com.bombombom.devs.study.model.Study.MIN_DIFFICULTY_LEVEL;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.ExternalApiApplication;
import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.algo.model.AlgorithmProblemFeedback;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.core.Spread;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.core.util.Util;
import com.bombombom.devs.external.algo.controller.dto.request.FeedbackAlgorithmProblemRequest;
import com.bombombom.devs.external.book.service.dto.SearchBooksResult;
import com.bombombom.devs.external.config.ElasticsearchTestConfig;
import com.bombombom.devs.external.study.controller.dto.request.AddAssignmentRequest;
import com.bombombom.devs.external.study.controller.dto.request.AddAssignmentRequest.NewAssignmentInfo;
import com.bombombom.devs.external.study.controller.dto.request.DeleteAssignmentRequest;
import com.bombombom.devs.external.study.controller.dto.request.EditAssignmentRequest;
import com.bombombom.devs.external.study.controller.dto.request.EditAssignmentRequest.AssignmentInfo;
import com.bombombom.devs.external.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.StartStudyRequest;
import com.bombombom.devs.external.study.controller.dto.response.AlgorithmStudyProgressResponse;
import com.bombombom.devs.external.study.controller.dto.response.AlgorithmStudyProgressResponse.AlgorithmProblemInfo;
import com.bombombom.devs.external.study.controller.dto.response.AlgorithmStudyTaskStatusResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyDetailsResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.external.study.service.dto.command.ConfigureStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.VoteAssignmentCommand;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.external.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.external.study.service.dto.result.StudyResult;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.study.enums.StudyStatus;
import com.bombombom.devs.study.enums.StudyType;
import com.bombombom.devs.study.enums.VotingProcess;
import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import com.bombombom.devs.study.model.AlgorithmProblemSolvedHistory;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.Assignment;
import com.bombombom.devs.study.model.AssignmentVote;
import com.bombombom.devs.study.model.BookStudy;
import com.bombombom.devs.study.model.Problem;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.UserAssignment;
import com.bombombom.devs.study.model.UserStudy;
import com.bombombom.devs.study.model.Video;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.AlgorithmProblemSolvedHistoryRepository;
import com.bombombom.devs.study.repository.AssignmentRepository;
import com.bombombom.devs.study.repository.AssignmentVoteRepository;
import com.bombombom.devs.study.repository.ProblemRepository;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserAssignmentRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.study.repository.VideoRepository;
import com.bombombom.devs.user.model.Role;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
    Clock clock;

    @Mock
    Clock mockClock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private UserStudyRepository userStudyRepository;

    @Autowired
    private AlgorithmProblemRepository algorithmProblemRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentVoteRepository assignmentVoteRepository;

    @Autowired
    private AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;

    @Autowired
    private AlgorithmProblemSolvedHistoryRepository algorithmProblemSolvedHistoryRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserAssignmentRepository userAssignmentRepository;

    @Nested
    @DisplayName("인증이 필요한 테스트")
    @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    class TestWithAuthentication {

        private User testuser;
        private Book testBook;

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
            testBook = Book.builder()
                .title("테스트용 책")
                .author("세계최강민석")
                .isbn(9023948901239L)
                .publisher("메가스터디")
                .tableOfContents("1. 2. 3. 4.")
                .build();

            bookRepository.save(testBook);
        }


        @Test
        @DisplayName("자신이 개설한 스터디 목록을 조회할 수 있다.")
        @WithUserDetails(value = "testuser",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void can_get_my_studies() throws Exception {
            /*
             * Given
             */
            LocalDate roundStartDate = LocalDate.of(2024, 7, 22);
            Study study = BookStudy.builder()
                .name("스터디")
                .introduce("안녕하세요")
                .headCount(1)
                .capacity(5)
                .penalty(10000)
                .reliabilityLimit(0)
                .startDate(roundStartDate)
                .weeks(2)
                .leader(testuser)
                .book(testBook)
                .votingProcess(VotingProcess.READY)
                .state(StudyStatus.RUNNING)
                .duplicated(false)
                .build();
            studyRepository.save(study);

            Study study2 = BookStudy.builder()
                .name("스터디")
                .introduce("안녕하세요")
                .headCount(1)
                .capacity(5)
                .penalty(10000)
                .reliabilityLimit(0)
                .startDate(roundStartDate)
                .weeks(2)
                .leader(testuser)
                .book(testBook)
                .votingProcess(VotingProcess.READY)
                .state(StudyStatus.RUNNING)
                .duplicated(false)
                .build();
            studyRepository.save(study2);
            /*
             * When
             */
            ResultActions resultActions = mockMvc.perform(
                get("/api/v1/studies/owned")
            );

            /*
             * Then
             */

            resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                    jsonPath("$.length()")
                        .value(equalTo(2)))
                .andExpect(
                    jsonPath("$[*].id",
                        containsInAnyOrder(study.getId().intValue(), study2.getId().intValue())));
        }

        @Nested
        @DisplayName("투표 통합 테스트")
        class VoteTest {

            @Test
            @DisplayName("중복할당 여부를 변경할 수 있다")
            @WithUserDetails(value = "testuser",
                setupBefore = TestExecutionEvent.TEST_EXECUTION)
            void can_set_duplicated() throws Exception {
                /*
                 * Given
                 */

                BookStudy study1 =
                    BookStudy.builder()
                        .reliabilityLimit(37)
                        .introduce("안녕하세요")
                        .name("스터디1")
                        .startDate(clock.today().plusWeeks(1))
                        .penalty(5000)
                        .weeks(5)
                        .state(StudyStatus.READY)
                        .headCount(0)
                        .leader(testuser)
                        .capacity(10)
                        .book(testBook)
                        .votingProcess(VotingProcess.READY)
                        .build();
                studyRepository.save(study1);

                /*
                 * When
                 */
                ConfigureStudyCommand configureStudyCommand = ConfigureStudyCommand.builder()
                    .duplicated(true)
                    .build();

                ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/studies/" + study1.getId() + "/config")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(configureStudyCommand))
                );


                /*
                 * Then
                 */
                resultActions.andDo(print())
                    .andExpect(status().isNoContent());
            }

            @Test
            @DisplayName("투표를 시작할 수 있다")
            @WithUserDetails(value = "testuser",
                setupBefore = TestExecutionEvent.TEST_EXECUTION)
            void can_start_vote() throws Exception {
                /*
                 * Given
                 */

                BookStudy study1 =
                    BookStudy.builder()
                        .reliabilityLimit(37)
                        .introduce("안녕하세요")
                        .name("스터디1")
                        .startDate(clock.today().plusWeeks(1))
                        .penalty(5000)
                        .weeks(5)
                        .state(StudyStatus.READY)
                        .headCount(0)
                        .leader(testuser)
                        .capacity(10)
                        .book(testBook)
                        .votingProcess(VotingProcess.READY)
                        .build();
                studyRepository.save(study1);

                /*
                 * When
                 */

                ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/studies/" + study1.getId() + "/start-voting")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                );


                /*
                 * Then
                 */
                resultActions.andDo(print())
                    .andExpect(status().isOk());
            }

            @Test
            @DisplayName("투표할 수 있다")
            @WithUserDetails(value = "testuser",
                setupBefore = TestExecutionEvent.TEST_EXECUTION)
            void can_vote() throws Exception {
                /*
                 * Given
                 */

                BookStudy study1 =
                    BookStudy.builder()
                        .reliabilityLimit(37)
                        .introduce("안녕하세요")
                        .name("스터디1")
                        .startDate(clock.today().plusWeeks(1))
                        .penalty(5000)
                        .weeks(5)
                        .state(StudyStatus.READY)
                        .headCount(0)
                        .leader(testuser)
                        .capacity(10)
                        .book(testBook)
                        .votingProcess(VotingProcess.ONGOING)
                        .build();
                study1.createRounds();

                List<Assignment> assignments = new ArrayList<>();

                assignments.add(
                    Assignment.builder()
                        .title("333")
                        .round(study1.getFirstRound())
                        .pageStart(132)
                        .pageEnd(140).build()
                );

                assignments.add(
                    Assignment.builder()
                        .title("가나다라")
                        .round(study1.getFirstRound())
                        .build()
                );

                studyRepository.save(study1);
                userStudyRepository.save(UserStudy.builder().user(testuser).study(study1).build());
                assignmentRepository.saveAll(assignments);

                /*
                 * When
                 */
                VoteAssignmentCommand voteAssignmentCommand = VoteAssignmentCommand.builder()
                    .first(assignments.getFirst().getId())
                    .second(assignments.getLast().getId())
                    .build();
                ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/studies/" + study1.getId() + "/vote")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteAssignmentCommand))
                );

                /*
                 * Then
                 */
                resultActions.andDo(print())
                    .andExpect(status().isOk());

            }


        }


        @Nested
        @DisplayName("과제 통합 테스트")
        class AssignmentTest {

            @Test
            @DisplayName("과제목록을 조회할 수 있다")
            void can_get_assignments()
                throws Exception {
                /*
                 * Given
                 */

                BookStudy study1 =
                    BookStudy.builder()
                        .reliabilityLimit(37)
                        .introduce("안녕하세요")
                        .name("스터디1")
                        .startDate(clock.today().plusWeeks(1))
                        .penalty(5000)
                        .weeks(5)
                        .state(StudyStatus.READY)
                        .headCount(0)
                        .leader(testuser)
                        .capacity(10)
                        .book(testBook)
                        .votingProcess(VotingProcess.READY)
                        .build();
                study1.createRounds();

                List<Assignment> assignments = new ArrayList<>();

                assignments.add(
                    Assignment.builder()
                        .title("333")
                        .round(study1.getFirstRound())
                        .pageStart(132)
                        .pageEnd(140).build()
                );

                assignments.add(
                    Assignment.builder()
                        .title("가나다라")
                        .round(study1.getFirstRound())
                        .build()
                );

                studyRepository.save(study1);
                assignmentRepository.saveAll(assignments);


                /*
                 * When
                 */
                ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/studies/" + study1.getId() + "/assignments")
                        .param("roundIdx", String.valueOf(0))
                        .contentType(MediaType.APPLICATION_JSON)
                );


                /*
                 * Then
                 */
                String expectedResponse = objectMapper.writeValueAsString(assignments);

                resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponse));

            }


            @Test
            @DisplayName("과제목록을 추가할 수 있다")
            @WithUserDetails(value = "testuser",
                setupBefore = TestExecutionEvent.TEST_EXECUTION)
            void can_add_assignments()
                throws Exception {
                /*
                 * Given
                 */

                BookStudy study1 =
                    BookStudy.builder()
                        .reliabilityLimit(37)
                        .introduce("안녕하세요")
                        .name("스터디1")
                        .startDate(clock.today().plusWeeks(1))
                        .penalty(5000)
                        .weeks(5)
                        .state(StudyStatus.READY)
                        .headCount(0)
                        .leader(testuser)
                        .capacity(10)
                        .book(testBook)
                        .votingProcess(VotingProcess.READY)
                        .build();
                study1.createRounds();

                studyRepository.save(study1);


                /*
                 * When
                 */

                List<NewAssignmentInfo> assignments = new ArrayList<>();

                assignments.add(
                    NewAssignmentInfo.builder()
                        .title("333")
                        .pageStart(132)
                        .pageEnd(140).build()
                );

                assignments.add(
                    NewAssignmentInfo.builder()
                        .title("가나다라")
                        .build()
                );

                AddAssignmentRequest addAssignmentRequest = AddAssignmentRequest.builder()
                    .roundIdx(0)
                    .assignments(assignments)
                    .build();
                ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/studies/" + study1.getId() + "/assignments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addAssignmentRequest))
                );


                /*
                 * Then
                 */

                resultActions.andDo(print())
                    .andExpect(status().isCreated());

            }


            @Test
            @DisplayName("과제목록을 수정할 수 있다")
            @WithUserDetails(value = "testuser",
                setupBefore = TestExecutionEvent.TEST_EXECUTION)
            void can_edit_assignments()
                throws Exception {
                /*
                 * Given
                 */

                BookStudy study1 =
                    BookStudy.builder()
                        .reliabilityLimit(37)
                        .introduce("안녕하세요")
                        .name("스터디1")
                        .startDate(clock.today().plusWeeks(1))
                        .penalty(5000)
                        .weeks(5)
                        .state(StudyStatus.READY)
                        .headCount(0)
                        .leader(testuser)
                        .capacity(10)
                        .book(testBook)
                        .votingProcess(VotingProcess.READY)
                        .build();
                study1.createRounds();

                List<Assignment> assignments = new ArrayList<>();

                assignments.add(
                    Assignment.builder()
                        .title("333")
                        .round(study1.getFirstRound())
                        .pageStart(132)
                        .pageEnd(140).build()
                );

                assignments.add(
                    Assignment.builder()
                        .title("가나다라")
                        .round(study1.getFirstRound())
                        .build()
                );

                studyRepository.save(study1);
                assignmentRepository.saveAll(assignments);


                /*
                 * When
                 */

                Assignment a = assignments.getFirst();
                List<AssignmentInfo> updatedAssignments = List.of(
                    AssignmentInfo.builder()
                        .id(a.getId())
                        .title(" 수정된타티을")
                        .description("설명 수정함")
                        .pageStart(113)
                        .pageEnd(140)
                        .build()
                );

                EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder()
                    .roundIdx(0)
                    .assignments(updatedAssignments)
                    .build();
                ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/studies/" + study1.getId() + "/assignments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editAssignmentRequest))
                );


                /*
                 * Then
                 */
                String expectedResponse = objectMapper.writeValueAsString(
                    updatedAssignments);

                resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponse));

            }


            @Test
            @DisplayName("과제목록을 삭제할 수 있다")
            @WithUserDetails(value = "testuser",
                setupBefore = TestExecutionEvent.TEST_EXECUTION)
            void can_delete_assignments()
                throws Exception {
                /*
                 * Given
                 */

                BookStudy study1 =
                    BookStudy.builder()
                        .reliabilityLimit(37)
                        .introduce("안녕하세요")
                        .name("스터디1")
                        .startDate(clock.today().plusWeeks(1))
                        .penalty(5000)
                        .weeks(5)
                        .state(StudyStatus.READY)
                        .headCount(0)
                        .leader(testuser)
                        .capacity(10)
                        .book(testBook)
                        .votingProcess(VotingProcess.READY)
                        .build();
                study1.createRounds();

                List<Assignment> assignments = new ArrayList<>();

                assignments.add(
                    Assignment.builder()
                        .title("333")
                        .round(study1.getFirstRound())
                        .pageStart(132)
                        .pageEnd(140).build()
                );

                assignments.add(
                    Assignment.builder()
                        .title("가나다라")
                        .round(study1.getFirstRound())
                        .build()
                );

                studyRepository.save(study1);
                assignmentRepository.saveAll(assignments);


                /*
                 * When
                 */

                DeleteAssignmentRequest deleteAssignmentRequest = DeleteAssignmentRequest.builder()
                    .roundIdx(0)
                    .assignmentIds(List.of(assignments.getFirst().getId()))
                    .build();
                ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/studies/" + study1.getId() + "/assignments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteAssignmentRequest))
                );


                /*
                 * Then
                 */
                resultActions.andDo(print())
                    .andExpect(status().isOk());

            }
        }

        @Nested
        @DisplayName("스터디 시작테스트")
        class StartStudyTest {

            @Test
            @DisplayName("알고리즘 스터디를 시작할 수 있다")
            @WithUserDetails(value = "testuser",
                setupBefore = TestExecutionEvent.TEST_EXECUTION)
            void can_start_algorithm_study()
                throws Exception {
                /*
                 * Given
                 */

                Integer difficultyGap = 5;
                Long difficultyBegin = 10L;
                AlgorithmStudy study1 =
                    AlgorithmStudy.builder()
                        .reliabilityLimit(37)
                        .introduce("안녕하세요")
                        .name("스터디1")
                        .startDate(clock.today().plusWeeks(1))
                        .penalty(5000)
                        .weeks(5)
                        .state(StudyStatus.READY)
                        .headCount(0)
                        .leader(testuser)
                        .capacity(10)
                        .difficultyGap(difficultyGap)
                        .problemCount(5)
                        .build();
                study1.createRounds();
                study1.setDifficulty(difficultyBegin.floatValue());

                studyRepository.save(study1);

                StartStudyRequest startStudyRequest = StartStudyRequest.builder()
                    .studyId(study1.getId())
                    .build();

                /*
                 * When
                 */
                ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/studies/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startStudyRequest))
                );


                /*
                 * Then
                 */
                resultActions.andDo(print())
                    .andExpect(status().isOk());

                AlgorithmStudy algorithmStudy = (AlgorithmStudy) studyRepository.findWithRoundsById(
                        study1.getId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

                Assertions.assertThat(algorithmStudy.getState())
                    .isEqualTo(StudyStatus.RUNNING);
                Assertions.assertThat(algorithmStudy.getStartDate())
                    .isEqualTo(clock.today());

                Assertions.assertThat(
                    algorithmStudy.getRounds().stream().map(
                        round -> round.getStartDate()
                    ).toList()
                ).isEqualTo(
                    IntStream.range(0, algorithmStudy.getWeeks())
                        .mapToObj(idx -> clock.today().plusWeeks(idx)).toList()
                );


            }

            @DisplayName("과제 중복할당 불가인 기술서적 스터디를 시작할 수 있다.")
            @Test
            @WithUserDetails(value = "testuser",
                setupBefore = TestExecutionEvent.TEST_EXECUTION)
            void can_start_book_study_study() throws Exception {
                /*
                 * Given
                 */
                final int roundIdx = 1;
                LocalDate roundStartDate = clock.today().plusWeeks(2);
                LocalDate roundEndDate = clock.today().plusWeeks(3);
                User user1 = User.builder()
                    .username("username1")
                    .password("password")
                    .role(Role.USER)
                    .reliability(50)
                    .build();
                User user2 = User.builder()
                    .username("username2")
                    .password("password")
                    .role(Role.USER)
                    .reliability(60)
                    .build();
                User user3 = User.builder()
                    .username("username3")
                    .password("password")
                    .role(Role.USER)
                    .reliability(60)
                    .build();
                userRepository.saveAll(List.of(user1, user2, user3));
                Book book = Book.builder()
                    .title("테스트용 책")
                    .author("세계최강민석")
                    .isbn(123456789L)
                    .publisher("메가스터디")
                    .tableOfContents("1. 2. 3. 4.")
                    .build();
                bookRepository.save(book);
                Study study = BookStudy.builder()
                    .name("스터디")
                    .introduce("안녕하세요")
                    .headCount(1)
                    .capacity(5)
                    .penalty(10000)
                    .reliabilityLimit(0)
                    .startDate(roundStartDate)
                    .weeks(2)
                    .leader(testuser)
                    .book(book)
                    .votingProcess(VotingProcess.READY)
                    .duplicated(false)
                    .state(StudyStatus.READY)
                    .build();
                studyRepository.save(study);
                Round round = Round.builder()
                    .idx(roundIdx)
                    .study(study)
                    .startDate(roundStartDate)
                    .endDate(roundEndDate)
                    .build();
                roundRepository.save(round);

                userStudyRepository.saveAll(List.of(
                    UserStudy.builder().user(user1).study(study).build(),
                    UserStudy.builder().user(user2).study(study).build(),
                    UserStudy.builder().user(user3).study(study).build()));

                List<Assignment> assignments = List.of(Assignment.builder()
                    .round(round)
                    .title("가나다")
                    .description("dldldl")
                    .build(), Assignment.builder()
                    .round(round)
                    .title("문제222")
                    .description("라일락하일락")
                    .build(), Assignment.builder()
                    .round(round)
                    .title("문3ㅔ33")
                    .description("통기닥통기닥")
                    .build());
                assignmentRepository.saveAll(assignments);

                Set<Assignment> availableAssignment = new HashSet<>(assignments);
                Assignment firstChoice = Util.getRandom(availableAssignment);
                availableAssignment.remove(firstChoice);
                Assignment secondChoice = Util.getRandom(availableAssignment);
                availableAssignment.remove(secondChoice);

                assignmentVoteRepository.saveAll(List.of(
                    AssignmentVote.builder().first(firstChoice).second(secondChoice)
                        .round(round).user(user1).build(),
                    AssignmentVote.builder().first(firstChoice).second(secondChoice)
                        .round(round).user(user2).build()
                ));


                /*
                 * When
                 */

                StartStudyRequest startStudyRequest = StartStudyRequest.builder()
                    .studyId(study.getId())
                    .build();

                ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/studies/start")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startStudyRequest))

                );

                /*
                 * Then
                 */

                resultActions.andDo(print())
                    .andExpect(status().isOk());

                Long user1Assigned = userAssignmentRepository.findWithAssignmentByUser(user1)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_ASSIGNMENT_NOT_FOUND))
                    .getAssignment().getId();
                Long user2Assigned = userAssignmentRepository.findWithAssignmentByUser(user2)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_ASSIGNMENT_NOT_FOUND))
                    .getAssignment().getId();
                Long user3Assigned = userAssignmentRepository.findWithAssignmentByUser(user3)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_ASSIGNMENT_NOT_FOUND))
                    .getAssignment().getId();

                Assertions.assertThat(List.of(user1Assigned, user2Assigned))
                    .containsExactlyInAnyOrder(firstChoice.getId(), secondChoice.getId());

                Assertions.assertThat(user3Assigned)
                    .isEqualTo(Util.getRandom(availableAssignment).getId());
            }

            @DisplayName("과제 중복할당 가능인 기술서적 스터디를 시작할 수 있다.")
            @Test
            @WithUserDetails(value = "testuser",
                setupBefore = TestExecutionEvent.TEST_EXECUTION)
            void can_start_book_study_study_with_duplication() throws Exception {
                /*
                 * Given
                 */
                final int roundIdx = 1;
                LocalDate roundStartDate = clock.today().plusWeeks(2);
                LocalDate roundEndDate = clock.today().plusWeeks(3);
                User user1 = User.builder()
                    .username("username1")
                    .password("password")
                    .role(Role.USER)
                    .reliability(50)
                    .build();
                User user2 = User.builder()
                    .username("username2")
                    .password("password")
                    .role(Role.USER)
                    .reliability(60)
                    .build();
                User user3 = User.builder()
                    .username("username3")
                    .password("password")
                    .role(Role.USER)
                    .reliability(60)
                    .build();
                userRepository.saveAll(List.of(user1, user2, user3));
                Book book = Book.builder()
                    .title("테스트용 책")
                    .author("세계최강민석")
                    .isbn(123456789L)
                    .publisher("메가스터디")
                    .tableOfContents("1. 2. 3. 4.")
                    .build();
                bookRepository.save(book);
                Study study = BookStudy.builder()
                    .name("스터디")
                    .introduce("안녕하세요")
                    .headCount(1)
                    .capacity(5)
                    .penalty(10000)
                    .reliabilityLimit(0)
                    .startDate(roundStartDate)
                    .weeks(2)
                    .leader(testuser)
                    .book(book)
                    .votingProcess(VotingProcess.READY)
                    .duplicated(true)
                    .state(StudyStatus.READY)
                    .build();
                studyRepository.save(study);
                Round round = Round.builder()
                    .idx(roundIdx)
                    .study(study)
                    .startDate(roundStartDate)
                    .endDate(roundEndDate)
                    .build();
                roundRepository.save(round);

                userStudyRepository.saveAll(List.of(
                    UserStudy.builder().user(user1).study(study).build(),
                    UserStudy.builder().user(user2).study(study).build(),
                    UserStudy.builder().user(user3).study(study).build()));

                List<Assignment> assignments = List.of(Assignment.builder()
                    .round(round)
                    .title("가나다")
                    .description("dldldl")
                    .build(), Assignment.builder()
                    .round(round)
                    .title("문제222")
                    .description("라일락하일락")
                    .build(), Assignment.builder()
                    .round(round)
                    .title("문3ㅔ33")
                    .description("통기닥통기닥")
                    .build());
                assignmentRepository.saveAll(assignments);

                Set<Assignment> availableAssignment = new HashSet<>(assignments);
                Assignment firstChoice = Util.getRandom(availableAssignment);
                availableAssignment.remove(firstChoice);
                Assignment secondChoice = Util.getRandom(availableAssignment);

                assignmentVoteRepository.saveAll(List.of(
                    AssignmentVote.builder().first(firstChoice).second(secondChoice)
                        .round(round).user(user1).build(),
                    AssignmentVote.builder().first(firstChoice).second(secondChoice)
                        .round(round).user(user2).build()
                ));


                /*
                 * When
                 */

                StartStudyRequest startStudyRequest = StartStudyRequest.builder()
                    .studyId(study.getId())
                    .build();

                ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/studies/start")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startStudyRequest))

                );

                /*
                 * Then
                 */

                resultActions.andDo(print())
                    .andExpect(status().isOk());

                Long user1Assigned = userAssignmentRepository.findWithAssignmentByUser(user1)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_ASSIGNMENT_NOT_FOUND))
                    .getAssignment().getId();
                Long user2Assigned = userAssignmentRepository.findWithAssignmentByUser(user2)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_ASSIGNMENT_NOT_FOUND))
                    .getAssignment().getId();
                Long user3Assigned = userAssignmentRepository.findWithAssignmentByUser(user3)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_ASSIGNMENT_NOT_FOUND))
                    .getAssignment().getId();

                Assertions.assertThat(user1Assigned)
                    .isEqualTo(firstChoice.getId());
                Assertions.assertThat(user2Assigned)
                    .isEqualTo(firstChoice.getId());

                Assertions.assertThat(user3Assigned)
                    .isIn(availableAssignment.stream().map(Assignment::getId).toList());
            }

        }

        @Test
        @DisplayName("알고리즘 문제에 대한 피드백을 줄 수 있다.")
        @WithUserDetails(value = "testuser",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void can_feedback() throws Exception {
            /*
             Given
             */

            Random gen = new Random();
            List<String> tags = AlgoTag.getTagNames();
            AlgoTag randomTag = AlgoTag.valueOf(tags.get(gen.nextInt(tags.size())));
            Integer difficultyGap = 10;
            Integer difficultyBegin = gen.nextInt(MIN_DIFFICULTY_LEVEL, MAX_DIFFICULTY_LEVEL);

            AlgorithmProblem problem = AlgorithmProblem.builder()
                .title("히스토그램에서 가장큰 직사각형")
                .refId(1023)
                .tag(randomTag)
                .difficulty(difficultyBegin + difficultyGap / 2)
                .build();

            algorithmProblemRepository.save(problem);

            AlgorithmStudy study =
                AlgorithmStudy.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .penalty(5000)
                    .weeks(5)
                    .state(StudyStatus.READY)
                    .headCount(0)
                    .leader(testuser)
                    .capacity(10)
                    .problemCount(5)
                    .difficultyGap(difficultyGap)
                    .startDate(clock.today())
                    .build();
            study.admit(testuser);
            study.createRounds();
            study.setDifficulty(difficultyBegin.floatValue());
            studyRepository.save(study);
            algorithmProblemAssignmentRepository.save(
                AlgorithmProblemAssignment.of(study.getFirstRound(), problem));

            algorithmProblemSolvedHistoryRepository.save(AlgorithmProblemSolvedHistory.builder()
                .tryCount(1)
                .solvedAt(clock.now())
                .problem(problem)
                .user(testuser)
                .build());

            FeedbackAlgorithmProblemRequest feedback = FeedbackAlgorithmProblemRequest.builder()
                .studyId(study.getId())
                .problemId(problem.getId())
                .again(true)
                .difficulty(4)
                .build();

            /*
             When
             */
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/studies/feedback")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(feedback))
            );

            /*
            Then
             */
            resultActions.andDo(print())
                .andExpect(status().isOk());

            AlgorithmStudy algorithmStudy = (AlgorithmStudy) studyRepository.findWithDifficultiesById(
                    feedback.studyId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

            Map<AlgoTag, Spread> difficultyMap =
                study.getDifficultySpreadMap();
            Float variance = study.getDifficultyVariance(
                AlgorithmProblemFeedback.builder()
                    .difficulty(feedback.difficulty())
                    .build()
            );
            Integer adjustedDifficulty = Math.round(difficultyBegin + variance);
            difficultyMap.put(randomTag,
                Spread.of(
                    Util.ensureRange(adjustedDifficulty, MIN_DIFFICULTY_LEVEL,
                        MAX_DIFFICULTY_LEVEL),
                    Util.ensureRange(adjustedDifficulty + difficultyGap, MIN_DIFFICULTY_LEVEL,
                        MAX_DIFFICULTY_LEVEL)
                ));

            Assertions.assertThat(algorithmStudy.getDifficultySpreadMap())
                .isEqualTo(difficultyMap);

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
                    .votingProcess(VotingProcess.READY)
                    .build();
            studyRepository.save(study);
            JoinStudyRequest request = JoinStudyRequest.builder()
                .studyId(study.getId()).build();

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
                    .startDate(LocalDate.now().plusWeeks(1))
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
                .startDate(registerAlgorithmStudyRequest.startDate())
                .penalty(1000)
                .weeks(5)
                .leader(profile)
                .state(StudyStatus.READY)
                .studyType(StudyType.ALGORITHM)
                .difficultyGap(5)
                .difficultySpreadMap(
                    AlgoTag.getTagNames().stream().collect(Collectors.toMap(AlgoTag::valueOf,
                        tag -> Spread.of(
                            registerAlgorithmStudyRequest.difficultyBegin(),
                            registerAlgorithmStudyRequest.difficultyEnd()
                        ))
                    ))
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
                    .startDate(LocalDate.now().plusWeeks(1))
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
                    .startDate(registerBookStudyRequest.startDate())
                    .penalty(1000)
                    .weeks(5)
                    .leader(profile)
                    .state(StudyStatus.READY)
                    .studyType(StudyType.BOOK)
                    .votingProcess(VotingProcess.READY)
                    .duplicated(false)
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
                .andExpect(jsonPath("$.message").value(ErrorCode.BOOK_NOT_FOUND.getMessage()));


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

                .capacity(10)
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
                .votingProcess(VotingProcess.READY)
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

    @DisplayName("알고리즘 스터디의 특정 회차 진행 현황을 조회할 수 있다.")
    @Test
    void can_retrieve_algorithm_study_progress() throws Exception {
        Long studyId = 1L;
        Integer roundIdx = 1;
        LocalDate roundStartDate = LocalDate.of(2024, 7, 22);
        LocalDate roundEndDate = LocalDate.of(2024, 7, 28);
        User user1 = User.builder()
            .id(1L)
            .username("username1")
            .password("password")
            .role(Role.USER)
            .reliability(50)
            .build();
        User user2 = User.builder()
            .id(2L)
            .username("username2")
            .password("password")
            .role(Role.USER)
            .reliability(60)
            .build();
        userRepository.saveAll(List.of(user1, user2));
        Study study = AlgorithmStudy.builder()
            .id(studyId)
            .name("스터디")
            .introduce("안녕하세요")
            .headCount(1)
            .capacity(5)
            .penalty(10000)
            .reliabilityLimit(0)
            .startDate(roundStartDate)
            .weeks(2)
            .leader(user1)
            .state(StudyStatus.RUNNING)
            .build();
        studyRepository.save(study);
        Round round = Round.builder()
            .id(1L)
            .idx(roundIdx)
            .study(study)
            .idx(roundIdx)
            .startDate(roundStartDate)
            .endDate(roundEndDate)
            .build();
        roundRepository.save(round);
        UserStudy userStudy1 = UserStudy.builder().user(user1).study(study).build();
        UserStudy userStudy2 = UserStudy.builder().user(user2).study(study).build();
        userStudyRepository.saveAll(List.of(userStudy1, userStudy2));
        AlgorithmProblem problem1 = AlgorithmProblem.builder()
            .id(1L)
            .refId(1000)
            .tag(AlgoTag.DP)
            .title("A")
            .link("https://www.test.com/1000")
            .difficulty(10)
            .build();
        AlgorithmProblem problem2 = AlgorithmProblem.builder()
            .id(2L)
            .refId(2000)
            .tag(AlgoTag.DP)
            .title("B")
            .link("https://www.test.com/2000")
            .difficulty(5)
            .build();
        algorithmProblemRepository.saveAll(List.of(problem1, problem2));
        AlgorithmProblemAssignment assignment1 = AlgorithmProblemAssignment.builder()
            .id(1L)
            .round(round)
            .problem(problem1)
            .build();
        AlgorithmProblemAssignment assignment2 = AlgorithmProblemAssignment.builder()
            .id(2L)
            .round(round)
            .problem(problem2)
            .build();
        AlgorithmProblemSolvedHistory history = AlgorithmProblemSolvedHistory.builder()
            .id(1L)
            .problem(problem2)
            .user(user1)
            .solvedAt(LocalDateTime.of(2024, 7, 23, 11, 0))
            .tryCount(2)
            .build();
        algorithmProblemAssignmentRepository.saveAll(List.of(assignment1, assignment2));
        algorithmProblemSolvedHistoryRepository.save(history);

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
        Map<Long, AlgorithmStudyTaskStatusResponse> users = new HashMap<>();
        Map<Long, Boolean> tasks1 = new HashMap<>();
        tasks1.put(1L, false);
        tasks1.put(2L, true);
        AlgorithmStudyTaskStatusResponse memberInfo1 = AlgorithmStudyTaskStatusResponse.builder()
            .username("username1")
            .isUpdating(false)
            .tasks(tasks1)
            .build();
        Map<Long, Boolean> tasks2 = new HashMap<>();
        tasks2.put(1L, false);
        tasks2.put(2L, false);
        AlgorithmStudyTaskStatusResponse memberInfo2 = AlgorithmStudyTaskStatusResponse.builder()
            .username("username2")
            .isUpdating(false)
            .tasks(tasks2)
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

    @DisplayName("알고리즘 스터디 상세 정보를 조회할 수 있다.")
    @Test
    void can_retrieve_algorithm_study_details() throws Exception {
        /*
        Given
         */
        Long studyId = 1L;
        Integer roundIdx = 1;
        LocalDate roundStartDate = LocalDate.of(2024, 7, 22);
        LocalDate roundEndDate = LocalDate.of(2024, 7, 28);
        User user1 = User.builder()
            .id(1L)
            .username("username1")
            .password("password")
            .role(Role.USER)
            .reliability(50)
            .build();
        User user2 = User.builder()
            .id(2L)
            .username("username2")
            .password("password")
            .role(Role.USER)
            .reliability(60)
            .build();
        userRepository.saveAll(List.of(user1, user2));
        AlgorithmStudy study = AlgorithmStudy.builder()
            .id(studyId)
            .name("스터디")
            .introduce("안녕하세요")
            .headCount(1)
            .capacity(5)
            .penalty(10000)
            .reliabilityLimit(0)
            .startDate(roundStartDate)
            .weeks(2)
            .leader(user1)
            .state(StudyStatus.RUNNING)
            .build();
        studyRepository.save(study);
        Round round = Round.builder()
            .id(1L)
            .idx(roundIdx)
            .study(study)
            .idx(roundIdx)
            .startDate(roundStartDate)
            .endDate(roundEndDate)
            .build();
        roundRepository.save(round);
        UserStudy userStudy1 = UserStudy.builder().user(user1).study(study).build();
        UserStudy userStudy2 = UserStudy.builder().user(user2).study(study).build();
        userStudyRepository.saveAll(List.of(userStudy1, userStudy2));
        AlgorithmProblem problem1 = AlgorithmProblem.builder()
            .id(1L)
            .refId(1000)
            .tag(AlgoTag.DP)
            .title("A")
            .link("https://www.test.com/1000")
            .difficulty(10)
            .build();
        AlgorithmProblem problem2 = AlgorithmProblem.builder()
            .id(2L)
            .refId(2000)
            .tag(AlgoTag.DP)
            .title("B")
            .link("https://www.test.com/2000")
            .difficulty(5)
            .build();
        algorithmProblemRepository.saveAll(List.of(problem1, problem2));
        AlgorithmProblemAssignment assignment1 = AlgorithmProblemAssignment.builder()
            .id(1L)
            .round(round)
            .problem(problem1)
            .build();
        AlgorithmProblemAssignment assignment2 = AlgorithmProblemAssignment.builder()
            .id(2L)
            .round(round)
            .problem(problem2)
            .build();
        AlgorithmProblemSolvedHistory history = AlgorithmProblemSolvedHistory.builder()
            .id(1L)
            .problem(problem2)
            .user(user1)
            .solvedAt(LocalDateTime.of(2024, 7, 23, 11, 0))
            .tryCount(2)
            .build();
        algorithmProblemAssignmentRepository.saveAll(List.of(assignment1, assignment2));
        algorithmProblemSolvedHistoryRepository.save(history);

        StudyResult studyResult = AlgorithmStudyResult.builder()
            .id(study.getId())
            .difficultySpreadMap(study.getDifficultySpreadMap())
            .studyType(StudyType.ALGORITHM)
            .name("스터디")
            .introduce("안녕하세요")
            .headCount(1)
            .capacity(5)
            .penalty(10000)
            .reliabilityLimit(0)
            .startDate(LocalDate.of(2024, 7, 22))
            .weeks(2)
            .leader(UserProfileResult.fromEntity(user1))
            .state(StudyStatus.RUNNING)
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
        Map<Long, AlgorithmStudyTaskStatusResponse> users = new HashMap<>();
        Map<Long, Boolean> tasks1 = new HashMap<>();
        tasks1.put(1L, false);
        tasks1.put(2L, true);
        AlgorithmStudyTaskStatusResponse memberInfo1 = AlgorithmStudyTaskStatusResponse.builder()
            .username("username1")
            .isUpdating(false)
            .tasks(tasks1)
            .build();
        Map<Long, Boolean> tasks2 = new HashMap<>();
        tasks2.put(1L, false);
        tasks2.put(2L, false);
        AlgorithmStudyTaskStatusResponse memberInfo2 = AlgorithmStudyTaskStatusResponse.builder()
            .username("username2")
            .isUpdating(false)
            .tasks(tasks2)
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
            .details(StudyResponse.fromResult(studyResult))
            .round(progressResponse)
            .build();

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

    @DisplayName("기술서적 스터디의 특정 회차 진행 현황을 조회할 수 있다.")
    @Test
    void can_retrieve_book_study_progress() throws Exception {
        /*
         * Given
         */
        final int roundIdx = 1;
        LocalDate roundStartDate = LocalDate.of(2024, 7, 22);
        LocalDate roundEndDate = LocalDate.of(2024, 7, 28);
        User user1 = User.builder()
            .username("username1")
            .password("password")
            .role(Role.USER)
            .reliability(50)
            .build();
        User user2 = User.builder()
            .username("username2")
            .password("password")
            .role(Role.USER)
            .reliability(60)
            .build();
        userRepository.saveAll(List.of(user1, user2));
        Book book = Book.builder()
            .title("테스트용 책")
            .author("세계최강민석")
            .isbn(123456789L)
            .publisher("메가스터디")
            .tableOfContents("1. 2. 3. 4.")
            .build();
        bookRepository.save(book);
        Study study = BookStudy.builder()
            .name("스터디")
            .introduce("안녕하세요")
            .headCount(1)
            .capacity(5)
            .penalty(10000)
            .reliabilityLimit(0)
            .startDate(roundStartDate)
            .weeks(2)
            .leader(user1)
            .book(book)
            .votingProcess(VotingProcess.READY)
            .state(StudyStatus.RUNNING)
            .duplicated(false)
            .build();
        studyRepository.save(study);
        Round round = Round.builder()
            .idx(roundIdx)
            .study(study)
            .startDate(roundStartDate)
            .endDate(roundEndDate)
            .build();
        roundRepository.save(round);

        UserStudy userStudy1 = UserStudy.builder().user(user1).study(study).build();
        UserStudy userStudy2 = UserStudy.builder().user(user2).study(study).build();
        userStudyRepository.saveAll(List.of(userStudy1, userStudy2));

        Assignment assignment1 = Assignment.builder()
            .round(round)
            .title("가나다")
            .description("dldldl")
            .build();
        Assignment assignment2 = Assignment.builder()
            .round(round)
            .title("문제222")
            .description("라일락하일락")
            .build();
        Assignment assignment3 = Assignment.builder()
            .round(round)
            .title("문3ㅔ33")
            .description("통기닥통기닥")
            .build();

        assignmentRepository.saveAll(List.of(assignment1, assignment2, assignment3));

        userAssignmentRepository.saveAll(List.of(
            UserAssignment.builder().user(user1).assignment(assignment1).build(),
            UserAssignment.builder().user(user2).assignment(assignment2).build()
        ));

        Problem problem1 = Problem.builder()
            .examiner(user1)
            .assignment(assignment1)
            .build();
        Problem problem2 = Problem.builder()
            .examiner(user2)
            .assignment(assignment2)
            .build();
        problemRepository.saveAll(List.of(problem1, problem2));

        Video video1 = Video.builder()
            .uploader(user1)
            .assignment(assignment1)
            .build();
        Video video2 = Video.builder()
            .uploader(user1)
            .assignment(assignment1)
            .build();
        Video video3 = Video.builder()
            .uploader(user2)
            .assignment(assignment2)
            .build();

        videoRepository.saveAll(List.of(video1, video2, video3));


        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            get("/api/v1/studies/progress/" + study.getId())
                .param("idx", String.valueOf(roundIdx))
        );

        /*
        Then
         */
        String userPath = "$.users.%s";

        String jsonForUser1 = objectMapper.writeValueAsString(
            Map.of("username", user1.getUsername(),
                "assignmentId", assignment1.getId(),
                "videoIds", List.of(video1.getId(), video2.getId()),
                "problemIds", List.of(problem1.getId())));
        String jsonForUser2 = objectMapper.writeValueAsString(
            Map.of("username", user2.getUsername(),
                "assignmentId", assignment2.getId(),
                "videoIds", List.of(video3.getId()),
                "problemIds", List.of(problem2.getId())));

        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(
                jsonPath(userPath, user1.getId())
                    .value(equalTo(JsonPath.read(jsonForUser1, "$"))))
            .andExpect(
                jsonPath(userPath, user2.getId())
                    .value(equalTo(JsonPath.read(jsonForUser2, "$"))));
    }

    @DisplayName("기술서적 스터디 상세 정보를 조회할 수 있다.")
    @Test
    void can_retrieve_book_study_details() throws Exception {
        /*
         * Given
         */
        final int roundIdx = 1;
        LocalDate roundStartDate = LocalDate.of(2024, 7, 22);
        LocalDate roundEndDate = LocalDate.of(2024, 7, 28);
        User user1 = User.builder()
            .username("username1")
            .password("password")
            .role(Role.USER)
            .reliability(50)
            .build();
        User user2 = User.builder()
            .username("username2")
            .password("password")
            .role(Role.USER)
            .reliability(60)
            .build();
        userRepository.saveAll(List.of(user1, user2));
        Book book = Book.builder()
            .title("테스트용 책")
            .author("세계최강민석")
            .isbn(123456789L)
            .publisher("메가스터디")
            .tableOfContents("1. 2. 3. 4.")
            .build();
        bookRepository.save(book);
        Study study = BookStudy.builder()
            .name("스터디")
            .introduce("안녕하세요")
            .headCount(1)
            .capacity(5)
            .penalty(10000)
            .reliabilityLimit(0)
            .startDate(roundStartDate)
            .weeks(2)
            .leader(user1)
            .book(book)
            .votingProcess(VotingProcess.READY)
            .state(StudyStatus.RUNNING)
            .duplicated(false)
            .build();
        studyRepository.save(study);
        Round round = Round.builder()
            .idx(roundIdx)
            .study(study)
            .startDate(roundStartDate)
            .endDate(roundEndDate)
            .build();
        roundRepository.save(round);

        UserStudy userStudy1 = UserStudy.builder().user(user1).study(study).build();
        UserStudy userStudy2 = UserStudy.builder().user(user2).study(study).build();
        userStudyRepository.saveAll(List.of(userStudy1, userStudy2));

        Assignment assignment1 = Assignment.builder()
            .round(round)
            .title("가나다")
            .description("dldldl")
            .build();
        Assignment assignment2 = Assignment.builder()
            .round(round)
            .title("문제222")
            .description("라일락하일락")
            .build();
        Assignment assignment3 = Assignment.builder()
            .round(round)
            .title("문3ㅔ33")
            .description("통기닥통기닥")
            .build();

        assignmentRepository.saveAll(List.of(assignment1, assignment2, assignment3));

        userAssignmentRepository.saveAll(List.of(
            UserAssignment.builder().user(user1).assignment(assignment1).build(),
            UserAssignment.builder().user(user2).assignment(assignment2).build()
        ));

        Problem problem1 = Problem.builder()
            .examiner(user1)
            .assignment(assignment1)
            .build();
        Problem problem2 = Problem.builder()
            .examiner(user2)
            .assignment(assignment2)
            .build();
        problemRepository.saveAll(List.of(problem1, problem2));

        Video video1 = Video.builder()
            .uploader(user1)
            .assignment(assignment1)
            .build();
        Video video2 = Video.builder()
            .uploader(user1)
            .assignment(assignment1)
            .build();
        Video video3 = Video.builder()
            .uploader(user2)
            .assignment(assignment2)
            .build();

        videoRepository.saveAll(List.of(video1, video2, video3));


        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            get("/api/v1/studies/" + study.getId())
        );

        /*
        Then
         */
        String userPath = "$.round.users.%s";

        String jsonForUser1 = objectMapper.writeValueAsString(
            Map.of("username", user1.getUsername(),
                "assignmentId", assignment1.getId(),
                "videoIds", List.of(video1.getId(), video2.getId()),
                "problemIds", List.of(problem1.getId())));
        String jsonForUser2 = objectMapper.writeValueAsString(
            Map.of("username", user2.getUsername(),
                "assignmentId", assignment2.getId(),
                "videoIds", List.of(video3.getId()),
                "problemIds", List.of(problem2.getId())));

        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(
                jsonPath(userPath, user1.getId())
                    .value(equalTo(JsonPath.read(jsonForUser1, "$"))))
            .andExpect(
                jsonPath(userPath, user2.getId())
                    .value(equalTo(JsonPath.read(jsonForUser2, "$"))));
    }
}
