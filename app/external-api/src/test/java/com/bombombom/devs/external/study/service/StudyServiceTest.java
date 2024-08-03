package com.bombombom.devs.external.study.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.DuplicationException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ForbiddenException;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.SystemClock;
import com.bombombom.devs.external.algo.service.dto.command.FeedbackAlgorithmProblemCommand;
import com.bombombom.devs.external.algo.service.dto.result.AlgorithmProblemResult;
import com.bombombom.devs.external.algo.service.dto.result.AlgorithmProblemSolveHistoryResult;
import com.bombombom.devs.external.study.controller.dto.request.EditAssignmentRequest.AssignmentInfo;
import com.bombombom.devs.external.study.service.dto.command.AddAssignmentCommand;
import com.bombombom.devs.external.study.service.dto.command.ConfigureStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.DeleteAssignmentCommand;
import com.bombombom.devs.external.study.service.dto.command.EditAssignmentCommand;
import com.bombombom.devs.external.study.service.dto.command.JoinStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.RegisterBookStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.StartStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.VoteAssignmentCommand;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.external.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.external.study.service.dto.result.RoundResult;
import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.StudyResult;
import com.bombombom.devs.external.study.service.dto.result.progress.AlgorithmStudyProgress;
import com.bombombom.devs.external.study.service.factory.StudyServiceFactory;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.study.enums.StudyStatus;
import com.bombombom.devs.study.enums.StudyType;
import com.bombombom.devs.study.enums.VotingProcess;
import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import com.bombombom.devs.study.model.AlgorithmProblemSolvedHistory;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.AlgorithmStudyDifficulty;
import com.bombombom.devs.study.model.Assignment;
import com.bombombom.devs.study.model.AssignmentVote;
import com.bombombom.devs.study.model.BookStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.UserStudy;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.AlgorithmProblemSolvedHistoryRepository;
import com.bombombom.devs.study.repository.AssignmentRepository;
import com.bombombom.devs.study.repository.AssignmentVoteRepository;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.user.model.Role;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Spy
    private SystemClock clock;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserStudyRepository userStudyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private StudyServiceFactory studyServiceFactory;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    AlgorithmProblemRepository algorithmProblemRepository;

    @Mock
    private AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;

    @Mock
    private AlgorithmProblemSolvedHistoryRepository algorithmProblemSolvedHistoryRepository;

    @Mock
    private AssignmentVoteRepository assignmentVoteRepository;

    @InjectMocks
    private AlgorithmStudyService algorithmStudyService;
    @InjectMocks
    private BookStudyService bookStudyService;

    @InjectMocks
    private StudyService studyService;

    @Test
    @DisplayName("readStudy 메소드는 Page<StudyResult>를 반환한다")
    void read_study_returns_page_of_study_result() throws Exception {
        /*
        Given
         */
        List<Study> repositoryResponses = new ArrayList<>();

        Book book = Book.builder()
            .title("테스트용 책")
            .author("세계최강민석")
            .isbn(123456789L)
            .publisher("메가스터디")
            .tableOfContents("1. 2. 3. 4.")
            .build();
        User leader = User.builder()
            .id(5L)
            .username("leader")
            .role(Role.USER)
            .introduce("introduce")
            .image("image")
            .reliability(50)
            .money(10000)
            .build();
        Study study1 =
            AlgorithmStudy.builder()
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .startDate(LocalDate.of(2024, 06, 14))
                .penalty(5000)
                .weeks(5)
                .leader(leader)
                .difficultyGap(5)
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
                .leader(leader)
                .penalty(5000)
                .weeks(5)
                .book(book)
                .build();

        repositoryResponses.add(study1);
        repositoryResponses.add(study2);

        Page<Study> studies = new PageImpl<>(repositoryResponses);
        when(studyRepository.findAllWithDifficultiesAndLeaderAndBook(
            any(Pageable.class))).thenReturn(studies);

        /*
        When
         */
        Page<StudyResult> studyResults = studyService.readStudy(PageRequest.of(0, 10));

        /*
        Then
         */
        List<StudyResult> studyList = repositoryResponses.stream()
            .map(StudyResult::fromEntity).toList();
        Page<StudyResult> expectedResponse = new PageImpl<>(studyList);

        Assertions.assertThat(studyResults).isEqualTo(expectedResponse);

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
        JoinStudyCommand joinStudyCommand = JoinStudyCommand.builder().studyId(study.getId())
            .build();
        when(userStudyRepository.existsByUserIdAndStudyId(testuser.getId(), study.getId()))
            .thenReturn(true);

        /*
         * When & Then
         */
        assertThatThrownBy(() -> studyService.joinStudy(
            testuser.getId(), joinStudyCommand))
            .isInstanceOf(BusinessRuleException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_JOINED);

    }

    @Test
    @DisplayName("createAlgorithmStudy 메소드는 AlgorithmStudyResult를 반환한다")
    void create_algorithm_study_returns_algorithm_study_result() throws Exception {
        /*
        Given
         */
        User testuser = User.builder()
            .id(1L)
            .username("testuser")
            .money(100000)
            .reliability(40)
            .build();

        RegisterAlgorithmStudyCommand registerAlgorithmStudyCommand =
            RegisterAlgorithmStudyCommand.builder()
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .capacity(10)
                .startDate(LocalDate.of(2024, 06, 19))
                .penalty(5000)
                .weeks(5)
                .headCount(0)
                .state(StudyStatus.READY)
                .difficultyBegin(10)
                .difficultyEnd(15)
                .problemCount(5).build();

        AlgorithmStudy algorithmStudy = AlgorithmStudy.builder()
            .reliabilityLimit(37)
            .introduce("안녕하세요")
            .name("스터디1")
            .headCount(1)
            .capacity(10)
            .startDate(LocalDate.of(2024, 06, 19))
            .penalty(5000)
            .weeks(5)
            .state(StudyStatus.READY)
            .leader(testuser)
            .difficultyGap(5)
            .problemCount(5)
            .difficulties(
                AlgoTag.getTagNames().stream().map(
                    tagName ->
                        AlgorithmStudyDifficulty.builder()
                            .algoTag(AlgoTag.valueOf(tagName))
                            .difficulty(
                                registerAlgorithmStudyCommand.difficultyBegin().floatValue())

                            .build()

                ).collect(Collectors.toList())
            )
            .build();

        when(userRepository.findById(testuser.getId())).thenReturn(Optional.of(testuser));


        /*
        When
         */
        AlgorithmStudyResult algorithmStudyResult = algorithmStudyService.createStudy(
            testuser.getId(),
            registerAlgorithmStudyCommand);

        /*
        Then
         */
        StudyResult expectedResponse = StudyResult.fromEntity(
            algorithmStudy);

        Assertions.assertThat(algorithmStudyResult).isEqualTo(expectedResponse);
    }


    @Test
    @DisplayName("createBookStudy 메소드는 BookStudyResult를 반환한다")
    void create_book_study_returns_book_study_result() throws Exception {
        /*
        Given
         */
        User testuser = User.builder()
            .id(1L)
            .username("testuser")
            .money(100000)
            .reliability(40)
            .build();
        Book book = Book.builder()
            .title("테스트용 책")
            .author("세계최강민석")
            .isbn(123456789L)
            .publisher("메가스터디")
            .tableOfContents("1. 2. 3. 4.")
            .build();

        RegisterBookStudyCommand registerBookStudyCommand =
            RegisterBookStudyCommand.builder()
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .capacity(10)
                .startDate(LocalDate.of(2024, 06, 19))
                .penalty(5000)
                .weeks(5)

                .state(StudyStatus.READY)
                .headCount(0)
                .isbn(123456789L)
                .build();

        BookStudy bookStudy = BookStudy.builder()
            .reliabilityLimit(37)
            .introduce("안녕하세요")
            .name("스터디1")
            .headCount(1)
            .capacity(10)
            .startDate(LocalDate.of(2024, 06, 19))
            .penalty(5000)
            .leader(testuser)
            .weeks(5)
            .book(book)
            .state(StudyStatus.READY)
            .votingProcess(VotingProcess.READY)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testuser));
        when(bookRepository.findByIsbn(123456789L)).thenReturn(Optional.of(book));

        /*
        When
         */
        BookStudyResult bookStudyResult = bookStudyService.createStudy(
            testuser.getId(),
            registerBookStudyCommand);

        /*
        Then
         */
        StudyResult expectedResponse = StudyResult.fromEntity(
            bookStudy);

        Assertions.assertThat(bookStudyResult).isEqualTo(expectedResponse);
    }

    @Nested
    @DisplayName("피드백 테스트")
    class FeedbackTest {

        @Test
        @DisplayName("problem을 찾지 못한 경우 피드백 적용이 실패한다")
        void apply_feedback_fail_if_problem_not_found() {

            /*
             * Given
             */

            FeedbackAlgorithmProblemCommand feedback = FeedbackAlgorithmProblemCommand.builder()
                .studyId(1L)
                .problemId(1L)
                .difficulty(2)
                .again(true)
                .build();

            Study study = mock(AlgorithmStudy.class);

            when(studyRepository.findById(feedback.studyId())).thenReturn(Optional.of(
                study));
            when(study.getStudyType()).thenReturn(StudyType.ALGORITHM);
            when(study.getId()).thenReturn(feedback.studyId());
            when(roundRepository.findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(study.getId(),
                clock.today())).thenReturn(Optional.of(mock(Round.class)));

            when(algorithmProblemRepository.findById(feedback.problemId())).thenReturn(
                Optional.empty());

            /*
             * When & Then
             */
            assertThatThrownBy(() -> algorithmStudyService.feedback(
                1L, feedback))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROBLEM_NOT_FOUND);

        }

        @Test
        @DisplayName("study을 찾지 못한 경우 피드백 적용이 실패한다")
        void apply_feedback_fail_if_study_not_found() {

            /*
             * Given
             */
            Study study = AlgorithmStudy.builder()
                .capacity(10)
                .headCount(1)
                .weeks(10)
                .reliabilityLimit(10)
                .penalty(1000)
                .state(StudyStatus.READY)
                .build();
            AlgorithmProblem algorithmProblem = AlgorithmProblem.builder()
                .id(1L)
                .link("https://www.bombombom.com")
                .title("에옹")
                .refId(10293)
                .tag(AlgoTag.DP)
                .build();

            FeedbackAlgorithmProblemCommand feedback = FeedbackAlgorithmProblemCommand.builder()
                .studyId(1L)
                .problemId(1L)
                .difficulty(2)
                .again(true)
                .build();
            when(studyRepository.findById(feedback.studyId())).thenReturn(
                Optional.empty());


            /*
             * When & Then
             */
            assertThatThrownBy(() -> algorithmStudyService.feedback(
                1L, feedback))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);

        }


        @Test
        @DisplayName("study가 기술서적 타입인 경우 피드백 적용이 실패한다")
        void apply_feedback_fail_if_study_type_is_book() {

            /*
             * Given
             */
            Study study = BookStudy.builder()
                .capacity(10)
                .headCount(1)
                .weeks(10)
                .reliabilityLimit(10)
                .penalty(1000)
                .state(StudyStatus.READY)
                .build();
            AlgorithmProblem algorithmProblem = AlgorithmProblem.builder()
                .id(1L)
                .link("https://www.bombombom.com")
                .title("에옹")
                .refId(10293)
                .tag(AlgoTag.DP)
                .build();

            FeedbackAlgorithmProblemCommand feedback = FeedbackAlgorithmProblemCommand.builder()
                .studyId(1L)
                .problemId(1L)
                .difficulty(2)
                .again(true)
                .build();
            when(studyRepository.findById(feedback.studyId())).thenReturn(
                Optional.of(study));


            /*
             * When & Then
             */
            assertThatThrownBy(() -> algorithmStudyService.feedback(
                1L, feedback))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WRONG_STUDY_TYPE);

        }

        @Test
        @DisplayName("study의 진행중인 라운드가 없는 경우 피드백 적용이 실패한다")
        void apply_feedback_fail_if_study_doesnt_have_ongoing_round() {

            /*
             * Given
             */
            Study study = AlgorithmStudy.builder()
                .id(1L)
                .capacity(10)
                .headCount(1)
                .weeks(10)
                .reliabilityLimit(10)
                .penalty(1000)
                .state(StudyStatus.READY)
                .build();
            AlgorithmProblem algorithmProblem = AlgorithmProblem.builder()
                .id(1L)
                .link("https://www.bombombom.com")
                .title("에옹")

                .refId(10293)
                .tag(AlgoTag.DP)
                .build();

            FeedbackAlgorithmProblemCommand feedback = FeedbackAlgorithmProblemCommand.builder()
                .studyId(1L)
                .problemId(1L)
                .difficulty(2)
                .again(true)
                .build();
            when(studyRepository.findById(feedback.studyId())).thenReturn(
                Optional.of(study));
            when(clock.today()).thenReturn(LocalDate.now());
            when(roundRepository.findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(
                study.getId(),
                clock.today())).thenReturn(Optional.empty());


            /*
             * When & Then
             */
            assertThatThrownBy(() -> algorithmStudyService.feedback(
                1L, feedback))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROUND_NOT_FOUND);

        }


        @Test
        @DisplayName("문제가 study의 진행중인 과제로 할당되지 않은 경우 피드백 적용이 실패한다")
        void apply_feedback_fail_if_problem_is_not_ongoing_assignment() {

            /*
             * Given
             */
            Study study = AlgorithmStudy.builder()
                .id(1L)
                .capacity(10)
                .headCount(1)
                .weeks(10)
                .reliabilityLimit(10)
                .penalty(1000)
                .state(StudyStatus.READY)
                .build();
            AlgorithmProblem algorithmProblem = AlgorithmProblem.builder()
                .id(1L)
                .link("https://www.bombombom.com")
                .title("에옹")
                .refId(10293)
                .tag(AlgoTag.DP)
                .build();
            Round round = Round.builder()
                .id(1L)
                .idx(1)
                .build();

            FeedbackAlgorithmProblemCommand feedback = FeedbackAlgorithmProblemCommand.builder()
                .studyId(1L)
                .problemId(1L)
                .difficulty(2)
                .again(true)
                .build();
            when(algorithmProblemRepository.findById(feedback.problemId())).thenReturn(
                Optional.of(algorithmProblem));
            when(studyRepository.findById(feedback.studyId())).thenReturn(
                Optional.of(study));
            when(clock.today()).thenReturn(LocalDate.now());
            when(roundRepository.findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(
                eq(study.getId()),
                any(LocalDate.class))).thenReturn(Optional.of(round));

            when(algorithmProblemAssignmentRepository.existsByRoundIdAndProblemId(
                round.getId(),
                algorithmProblem.getId())).thenReturn(false);

            AlgorithmProblemSolvedHistory history = mock(AlgorithmProblemSolvedHistory.class);

            LocalDateTime now = LocalDateTime.now();

            when(history.getSolvedAt()).thenReturn(now);
            when(algorithmProblemSolvedHistoryRepository.findByUserIdAndProblemId(
                1L, algorithmProblem.getId()
            )).thenReturn(Optional.of(history));

            /*
            * When & Then
            */
            assertThatThrownBy(() -> algorithmStudyService.feedback(
                1L, feedback))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ASSIGNMENT_NOT_FOUND);

        }


        @Test
        @DisplayName("유저가 스터디의 멤버가 아닌 경우 피드백 적용이 실패한다")
        void apply_feedback_fail_if_user_is_not_a_member() {

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
                .id(3L)
                .capacity(10)
                .headCount(1)
                .weeks(10)
                .reliabilityLimit(10)
                .penalty(1000)
                .state(StudyStatus.READY)
                .build();
            AlgorithmProblem algorithmProblem = AlgorithmProblem.builder()
                .id(1L)
                .link("https://www.bombombom.com")
                .title("에옹")
                .refId(10293)
                .tag(AlgoTag.DP)
                .build();
            Round round = Round.builder()
                .id(1L)
                .idx(1)
                .build();

            FeedbackAlgorithmProblemCommand feedback = FeedbackAlgorithmProblemCommand.builder()
                .studyId(1L)
                .problemId(1L)
                .difficulty(2)
                .again(true)
                .build();
            when(algorithmProblemRepository.findById(feedback.problemId())).thenReturn(
                Optional.of(algorithmProblem));
            when(studyRepository.findById(feedback.studyId())).thenReturn(
                Optional.of(study));
            when(clock.today()).thenReturn(LocalDate.now());
            when(roundRepository.findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(
                eq(study.getId()),
                any(LocalDate.class))).thenReturn(Optional.of(round));

            when(algorithmProblemAssignmentRepository.existsByRoundIdAndProblemId(
                round.getId(),
                algorithmProblem.getId())).thenReturn(true);

            AlgorithmProblemSolvedHistory history = mock(AlgorithmProblemSolvedHistory.class);

            LocalDateTime now = LocalDateTime.now();

            when(history.getSolvedAt()).thenReturn(now);
            when(algorithmProblemSolvedHistoryRepository.findByUserIdAndProblemId(
                testuser.getId(), algorithmProblem.getId()
            )).thenReturn(Optional.of(history));

            when(userStudyRepository.existsByUserIdAndStudyId(
                testuser.getId(), study.getId()
            )).thenReturn(false);
            /*
             * When & Then
             */
            assertThatThrownBy(() -> algorithmStudyService.feedback(
                testuser.getId(), feedback))
                .isInstanceOf(ForbiddenException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ONLY_MEMBER_ALLOWED);

        }


        @Test
        @DisplayName("유저를 찾지못한 경우 피드백 적용이 실패한다")
        void apply_feedback_fail_if_user_not_found() {
            User testuser = User.builder()
                .id(1L)
                .username("testuser")
                .money(100000)
                .reliability(10)
                .build();
            Study study = AlgorithmStudy.builder()
                .id(3L)
                .capacity(10)
                .headCount(1)
                .weeks(10)
                .reliabilityLimit(10)
                .penalty(1000)
                .state(StudyStatus.READY)
                .build();
            AlgorithmProblem algorithmProblem = AlgorithmProblem.builder()
                .id(1L)
                .link("https://www.bombombom.com")
                .title("에옹")
                .refId(10293)
                .tag(AlgoTag.DP)
                .build();
            Round round = Round.builder()
                .id(1L)
                .idx(1)
                .build();

            FeedbackAlgorithmProblemCommand feedback = FeedbackAlgorithmProblemCommand.builder()
                .studyId(1L)
                .problemId(1L)
                .difficulty(2)
                .again(true)
                .build();
            when(algorithmProblemRepository.findById(feedback.problemId())).thenReturn(
                Optional.of(algorithmProblem));
            when(studyRepository.findById(feedback.studyId())).thenReturn(
                Optional.of(study));
            when(clock.today()).thenReturn(LocalDate.now());
            when(roundRepository.findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(
                eq(study.getId()),
                any(LocalDate.class))).thenReturn(Optional.of(round));

            when(algorithmProblemAssignmentRepository.existsByRoundIdAndProblemId(
                round.getId(),
                algorithmProblem.getId())).thenReturn(true);

            when(userStudyRepository.existsByUserIdAndStudyId(
                testuser.getId(), study.getId()
            )).thenReturn(true);

            when(userRepository.findById(
                testuser.getId()
            )).thenReturn(Optional.empty());

            AlgorithmProblemSolvedHistory history = mock(AlgorithmProblemSolvedHistory.class);

            LocalDateTime now = LocalDateTime.now();

            when(history.getSolvedAt()).thenReturn(now);
            when(algorithmProblemSolvedHistoryRepository.findByUserIdAndProblemId(
                testuser.getId(), algorithmProblem.getId()
            )).thenReturn(Optional.of(history));

            /*
             * When & Then
             */
            assertThatThrownBy(() -> algorithmStudyService.feedback(
                testuser.getId(), feedback))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }


        @Test
        @DisplayName("유저의 문제 풀이 기록을 찾을 수 없으면 피드백 적용이 실패한다")
        void apply_feedback_fail_if_solve_history_not_found() {

            /*
             * Given
             */

            FeedbackAlgorithmProblemCommand feedback = FeedbackAlgorithmProblemCommand.builder()
                .studyId(1L)
                .problemId(1L)
                .difficulty(2)
                .again(true)
                .build();

            Long userId = 10L;
            AlgorithmProblem problem = mock(AlgorithmProblem.class);
            when(problem.getId()).thenReturn(feedback.problemId());

            when(algorithmProblemRepository.findById(feedback.problemId())).thenReturn(
                Optional.of(problem));

            AlgorithmStudy study = mock(AlgorithmStudy.class);
            when(study.getId()).thenReturn(feedback.studyId());
            when(study.getStudyType()).thenReturn(StudyType.ALGORITHM);

            when(studyRepository.findById(feedback.studyId())).thenReturn(
                Optional.of(study));

            when(clock.today()).thenReturn(LocalDate.now());

            Round round = mock(Round.class);

            when(roundRepository.findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(
                eq(study.getId()),
                any(LocalDate.class))).thenReturn(Optional.of(round));

            AlgorithmProblemSolvedHistory history = mock(AlgorithmProblemSolvedHistory.class);

            LocalDateTime now = LocalDateTime.now();

            when(algorithmProblemSolvedHistoryRepository.findByUserIdAndProblemId(
                userId, problem.getId()
            )).thenReturn(Optional.empty());

            /*
             * When & Then
             */
            assertThatThrownBy(() -> algorithmStudyService.feedback(
                userId, feedback))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SOLVE_HISTORY_NOT_FOUND);
        }


        @Test
        @DisplayName("유저가 문제를 풀지 않았다면 피드백 적용이 실패한다")
        void apply_feedback_fail_if_unsolved() {

            /*
             * Given
             */

            FeedbackAlgorithmProblemCommand feedback = FeedbackAlgorithmProblemCommand.builder()
                .studyId(1L)
                .problemId(1L)
                .difficulty(2)
                .again(true)
                .build();

            Long userId = 10L;
            AlgorithmProblem problem = mock(AlgorithmProblem.class);
            when(problem.getId()).thenReturn(feedback.problemId());

            when(algorithmProblemRepository.findById(feedback.problemId())).thenReturn(
                Optional.of(problem));

            AlgorithmStudy study = mock(AlgorithmStudy.class);
            when(study.getId()).thenReturn(feedback.studyId());
            when(study.getStudyType()).thenReturn(StudyType.ALGORITHM);

            when(studyRepository.findById(feedback.studyId())).thenReturn(
                Optional.of(study));

            when(clock.today()).thenReturn(LocalDate.now());

            Round round = mock(Round.class);

            when(roundRepository.findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(
                eq(study.getId()),
                any(LocalDate.class))).thenReturn(Optional.of(round));

            AlgorithmProblemSolvedHistory history = mock(AlgorithmProblemSolvedHistory.class);

            LocalDateTime now = LocalDateTime.now();

            when(algorithmProblemSolvedHistoryRepository.findByUserIdAndProblemId(
                userId, problem.getId()
            )).thenReturn(Optional.of(history));

            /*
             * When & Then
             */
            assertThatThrownBy(() -> algorithmStudyService.feedback(
                userId, feedback))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROBLEM_NOT_SOLVED);
        }
    }

    @Nested
    @DisplayName("스터디 진행현황")
    class StudyProgressTest {

        @Nested
        @DisplayName("알고리즘 스터디 진행현황")
        class AlgorithmStudyProgressTest {

        @DisplayName("study id와 round 순서번호로 특정 회차의 알고리즘 스터디 진행 현황 결과를 반환할 수 있다.")
        @Test
        void retrieve_algorithm_study_progress_by_study_id_and_round_idx() {
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
                .role(Role.USER)
                .reliability(50)
                .build();
            User user2 = User.builder()
                .id(2L)
                .username("username2")
                .role(Role.USER)
                .reliability(60)
                .build();
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
            Round round = Round.builder()
                .id(1L)
                .idx(roundIdx)
                .study(study)
                .idx(roundIdx)
                .startDate(roundStartDate)
                .endDate(roundEndDate)
                .build();
            UserStudy userStudy1 = UserStudy.builder().user(user1).study(study).build();
            UserStudy userStudy2 = UserStudy.builder().user(user2).study(study).build();
            List<UserStudy> members = List.of(userStudy1, userStudy2);
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

                UserProfileResult userProfileResult1 = UserProfileResult.builder()
                    .id(1L)
                    .username("username1")
                    .role(Role.USER)
                    .reliability(50)
                    .build();
                UserProfileResult userProfileResult2 = UserProfileResult.builder()
                    .id(2L)
                    .username("username2")
                    .role(Role.USER)
                    .reliability(60)
                    .build();
                RoundResult roundResult = RoundResult.builder()
                    .idx(roundIdx)
                    .startDate(roundStartDate)
                    .endDate(roundEndDate)
                    .build();
                AlgorithmProblemResult algorithmProblemResult1 = AlgorithmProblemResult.builder()
                    .id(1L)
                    .refId(1000)
                    .tag(AlgoTag.DP)
                    .title("A")
                    .link("https://www.test.com/1000")
                    .difficulty(10)
                    .build();
                AlgorithmProblemResult algorithmProblemResult2 = AlgorithmProblemResult.builder()
                    .id(2L)
                    .refId(2000)
                    .tag(AlgoTag.DP)
                    .title("B")
                    .link("https://www.test.com/2000")
                    .difficulty(5)
                    .build();
                AlgorithmProblemSolveHistoryResult historyResult = AlgorithmProblemSolveHistoryResult.builder()
                    .problemId(2L)
                    .userId(1L)
                    .solvedAt(LocalDateTime.of(2024, 7, 23, 11, 0))
                    .tryCount(2)
                    .build();
                AlgorithmStudyProgress algorithmStudyProgress = AlgorithmStudyProgress.builder()
                    .round(roundResult)
                    .algorithmProblems(List.of(algorithmProblemResult1, algorithmProblemResult2))
                    .histories(List.of(historyResult))
                    .build();
                StudyProgressResult studyProgressResult = StudyProgressResult.builder()
                    .studyType(StudyType.ALGORITHM)
                    .members(List.of(userProfileResult1, userProfileResult2))
                    .studyProgress(algorithmStudyProgress)
                    .build();

            doReturn(Optional.of(study)).when(studyRepository).findById(anyLong());
            doReturn(Optional.of(round)).when(roundRepository)
                .findRoundByStudyAndIdx(anyLong(), anyInt());
            doReturn(members).when(userStudyRepository).findWithUserByStudyId(anyLong());
            doReturn(algorithmStudyService).when(studyServiceFactory).getService(StudyType.ALGORITHM);
            doReturn(List.of(assignment1, assignment2)).when(algorithmProblemAssignmentRepository)
                .findAssignmentWithProblemByRoundId(anyLong());
            doReturn(List.of(history)).when(algorithmProblemSolvedHistoryRepository)
                .findSolvedHistoryWithUserAndProblem(anyList(), anyList());

                /*
                 * When & Then
                 */
                Assertions.assertThat(studyService.findStudyProgress(studyId, roundIdx))
                    .isEqualTo(studyProgressResult);
            }

            @DisplayName("존재하지 않는 알고리즘 스터디인 경우 진행 현황 조회에 실패한다.")
            @Test
            void retrieve_algorithm_study_progress_with_non_existent_study_fail() {
                /*
                 * Given
                 */
                Long studyId = 1L;
                Integer roundIdx = 1;

                doReturn(Optional.empty()).when(studyRepository)
                    .findWithDifficultiesAndLeaderAndBookById(anyLong());

                /*
                 * When & Then
                 */
                assertThatThrownBy(
                    () -> studyService.findStudyProgress(studyId, roundIdx))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);
            }

            @DisplayName("해당 회차 정보가 존재하지 않는 경우 알고리즘 스터디 진행 현황 조회에 실패한다.")
            @Test
            void retrieve_algorithm_study_progress_with_non_existent_round_fail() {
                /*
                 *Given
                 */
                Long studyId = 1L;
                Integer roundIdx = 1;
                LocalDate studyStartDate = LocalDate.of(2024, 7, 22);
                User user1 = User.builder()
                    .id(1L)
                    .username("username1")
                    .role(Role.USER)
                    .reliability(50)
                    .build();
                Study study = AlgorithmStudy.builder()
                    .id(studyId)
                    .name("스터디")
                    .introduce("안녕하세요")
                    .headCount(1)
                    .capacity(5)
                    .penalty(10000)
                    .reliabilityLimit(0)
                    .startDate(studyStartDate)
                    .weeks(2)
                    .leader(user1)
                    .state(StudyStatus.RUNNING)
                    .build();

                doReturn(Optional.of(study)).when(studyRepository)
                    .findWithDifficultiesAndLeaderAndBookById(anyLong());
                doReturn(Optional.empty()).when(roundRepository)
                    .findRoundByStudyAndIdx(anyLong(), anyInt());

                /*
                 * When & Then
                 */
                assertThatThrownBy(() -> studyService.findStudyProgress(studyId, roundIdx))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROUND_NOT_FOUND);

            }

        @DisplayName("study id로 해당 알고리즘 스터디의 정보 조회 결과를 반환할 수 있다.")
        @Test
        void retrieve_algorithm_study_details_by_study_id() {
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
                .role(Role.USER)
                .reliability(50)
                .build();
            User user2 = User.builder()
                .id(2L)
                .username("username2")
                .role(Role.USER)
                .reliability(60)
                .build();
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
            Round round = Round.builder()
                .id(1L)
                .idx(roundIdx)
                .study(study)
                .idx(roundIdx)
                .startDate(roundStartDate)
                .endDate(roundEndDate)
                .build();
            UserStudy userStudy1 = UserStudy.builder().user(user1).study(study).build();
            UserStudy userStudy2 = UserStudy.builder().user(user2).study(study).build();
            List<UserStudy> members = List.of(userStudy1, userStudy2);
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

                UserProfileResult userProfileResult1 = UserProfileResult.builder()
                    .id(1L)
                    .username("username1")
                    .role(Role.USER)
                    .reliability(50)
                    .build();
                UserProfileResult userProfileResult2 = UserProfileResult.builder()
                    .id(2L)
                    .username("username2")
                    .role(Role.USER)
                    .reliability(60)
                    .build();
                RoundResult roundResult = RoundResult.builder()
                    .idx(roundIdx)
                    .startDate(roundStartDate)
                    .endDate(roundEndDate)
                    .build();
                AlgorithmProblemResult algorithmProblemResult1 = AlgorithmProblemResult.builder()
                    .id(1L)
                    .refId(1000)
                    .tag(AlgoTag.DP)
                    .title("A")
                    .link("https://www.test.com/1000")
                    .difficulty(10)
                    .build();
                AlgorithmProblemResult algorithmProblemResult2 = AlgorithmProblemResult.builder()
                    .id(2L)
                    .refId(2000)
                    .tag(AlgoTag.DP)
                    .title("B")
                    .link("https://www.test.com/2000")
                    .difficulty(5)
                    .build();
                AlgorithmProblemSolveHistoryResult historyResult = AlgorithmProblemSolveHistoryResult.builder()
                    .problemId(2L)
                    .userId(1L)
                    .solvedAt(LocalDateTime.of(2024, 7, 23, 11, 0))
                    .tryCount(2)
                    .build();
                AlgorithmStudyProgress algorithmStudyProgress = AlgorithmStudyProgress.builder()
                    .round(roundResult)
                    .algorithmProblems(List.of(algorithmProblemResult1, algorithmProblemResult2))
                    .histories(List.of(historyResult))
                    .build();
                StudyProgressResult studyProgressResult = StudyProgressResult.builder()
                    .studyType(StudyType.ALGORITHM)
                    .members(List.of(userProfileResult1, userProfileResult2))
                    .studyProgress(algorithmStudyProgress)
                    .build();

                StudyResult studyResult = AlgorithmStudyResult.builder()
                    .id(study.getId())
                    .studyType(StudyType.ALGORITHM)
                    .difficultySpreadMap(Map.of())
                    .name("스터디")
                    .introduce("안녕하세요")
                    .headCount(1)
                    .capacity(5)
                    .penalty(10000)
                    .reliabilityLimit(0)
                    .startDate(roundStartDate)
                    .weeks(2)
                    .leader(userProfileResult1)
                    .state(StudyStatus.RUNNING)
                    .build();
                StudyDetailsResult studyDetailsResult = StudyDetailsResult.builder()
                    .studyResult(studyResult)
                    .currentStudyProgress(studyProgressResult)
                    .build();

            doReturn(Optional.of(study)).when(studyRepository).findById(anyLong());
            doReturn(Optional.of(round)).when(roundRepository)
                .findRoundByStudyIdAndBetweenStartDateAndEndDateOrIdx(anyLong(), anyInt(),
                    any(LocalDate.class));
            doReturn(members).when(userStudyRepository).findWithUserByStudyId(anyLong());
            doReturn(algorithmStudyService).when(studyServiceFactory).getService(StudyType.ALGORITHM);
            doReturn(List.of(assignment1, assignment2)).when(algorithmProblemAssignmentRepository)
                .findAssignmentWithProblemByRoundId(anyLong());
            doReturn(List.of(history)).when(algorithmProblemSolvedHistoryRepository)
                .findSolvedHistoryWithUserAndProblem(anyList(), anyList());

                /*
                 *  When & Then
                 */
                Assertions.assertThat(studyService.findStudyDetails(studyId))
                    .isEqualTo(studyDetailsResult);
            }

            @DisplayName("존재하지 않는 알고리즘 스터디인 경우 스터디 정보 조회에 실패한다.")
            @Test
            void find_study_details_with_non_existent_study_fail() {
                /*
                 * Given
                 */
                Long studyId = 1L;

                doReturn(Optional.empty()).when(studyRepository)
                    .findWithDifficultiesAndLeaderAndBookById(studyId);

                /*
                 * When & Then
                 */
                assertThatThrownBy(() -> studyService.findStudyDetails(studyId))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);
            }

            @DisplayName("회차 정보가 존재하지 않는 경우 알고리즘 스터디 정보 조회에 실패한다.")
            @Test
            void retrieve_algorithm_study_details_with_non_existent_round_fail() {
                /*
                 *   Given
                 */
                Long studyId = 1L;
                LocalDate studyStartDate = LocalDate.of(2024, 7, 22);
                User user1 = User.builder()
                    .id(1L)
                    .username("username1")
                    .role(Role.USER)
                    .reliability(50)
                    .build();
                Study study = AlgorithmStudy.builder()
                    .id(studyId)
                    .name("스터디")
                    .introduce("안녕하세요")
                    .headCount(1)
                    .capacity(5)
                    .penalty(10000)
                    .reliabilityLimit(0)
                    .startDate(studyStartDate)
                    .weeks(2)
                    .leader(user1)
                    .state(StudyStatus.RUNNING)
                    .build();

                doReturn(Optional.of(study)).when(studyRepository)
                    .findWithDifficultiesAndLeaderAndBookById(studyId);
                doReturn(Optional.empty()).when(roundRepository)
                    .findRoundByStudyIdAndBetweenStartDateAndEndDateOrIdx(eq(studyId), anyInt(),
                        any(LocalDate.class));

                /*
                 * When & Then
                 */

                assertThatThrownBy(() -> studyService.findStudyDetails(studyId))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROUND_NOT_FOUND);
            }

        }


        @Nested
        @DisplayName("기술서적 스터디 진행현황")
        class BookStudyProgressTest {

            @DisplayName("스터디를 찾지못한경우 기술서적 스터디 상세정보 조회는 실패한다")
            @Test
            void find_study_progress_fail_if_study_not_found() {
                /*
                 * Given
                 */
                Long studyId = 1L;
                Integer roundIdx = 2;

                when(studyRepository.findWithDifficultiesAndLeaderAndBookById(studyId))
                    .thenReturn(
                        Optional.empty()
                    );

                /*
                 * When & Then
                 */
                assertThatThrownBy(() -> studyService.findStudyDetails(studyId))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);


            }

            @DisplayName("최근 회차를 찾지 못한 경우 기술서적 스터디 상세정보 조회는 실패한다")
            @Test
            void find_study_progress_fail_if_latest_round_not_found() {
                /*
                 * Given
                 */
                Long studyId = 1L;

                int studyWeeks = 5;
                Study study = BookStudy.builder()
                    .weeks(studyWeeks)
                    .build();

                when(studyRepository.findWithDifficultiesAndLeaderAndBookById(studyId))
                    .thenReturn(
                        Optional.of(study)
                    );

                when(roundRepository.findRoundByStudyIdAndBetweenStartDateAndEndDateOrIdx(
                    eq(studyId), eq(studyWeeks - 1), any(LocalDate.class)))
                    .thenReturn(
                        Optional.empty()
                    );


                /*
                 * When & Then
                 */
                assertThatThrownBy(() -> studyService.findStudyDetails(studyId))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROUND_NOT_FOUND);


            }

        }

    }

    @Nested
    @DisplayName("스터디시작 테스트")
    class StartStudyTest {

        @DisplayName("스터디 시작은 스터디를 찾지 못한 경우 실패한다")
        @Test
        void start_study_fails_if_study_not_found() {
            /*
             * Given
             */
            Long userId = 1L;

            StartStudyCommand startStudyCommand = StartStudyCommand.builder()
                .studyId(3L)
                .build();

            when(studyRepository.findWithLeaderById(startStudyCommand.studyId()))
                .thenReturn(
                    Optional.empty()
                );

            /*
             * When & Then
             */
            assertThatThrownBy(() -> studyService.start(userId, startStudyCommand))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);
        }

        @DisplayName("스터디 시작은 리더가 아닌 경우 실패한다")
        @Test
        void start_study_fails_if_user_is_not_leader() {
            /*
             * Given
             */
            Long userId = 1L;

            StartStudyCommand startStudyCommand = StartStudyCommand.builder()
                .studyId(3L)
                .build();

            Study study = AlgorithmStudy.builder()
                .id(startStudyCommand.studyId())
                .leader(User.builder()
                    .id(userId + 1).build())
                .build();

            when(studyRepository.findWithLeaderById(startStudyCommand.studyId()))
                .thenReturn(
                    Optional.of(study)
                );

            /*
             * When & Then
             */
            assertThatThrownBy(() -> studyService.start(userId, startStudyCommand))
                .isInstanceOf(ForbiddenException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ONLY_LEADER_ALLOWED);
        }

        @DisplayName("스터디가 이미 시작한 경우 스터디 시작은 실패한다")
        @Test
        void start_study_fails_if_study_already_started() {
            /*
             * Given
             */
            Long userId = 1L;

            StartStudyCommand startStudyCommand = StartStudyCommand.builder()
                .studyId(3L)
                .build();

            Study study = AlgorithmStudy.builder()
                .id(startStudyCommand.studyId())
                .leader(User.builder()
                    .id(userId).build())
                .state(StudyStatus.RUNNING)
                .build();

            when(studyRepository.findWithLeaderById(startStudyCommand.studyId()))
                .thenReturn(
                    Optional.of(study)
                );

            /*
             * When & Then
             */
            assertThatThrownBy(() -> studyService.start(userId, startStudyCommand))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_STARTED);
        }

    }

    @Nested
    @DisplayName("과제 테스트")
    class AssignmentTest {

        @Nested
        @DisplayName("과제추가 테스트")
        class AddAssignment {

            @DisplayName("다음회차가 없는 경우 과제목록 추가에 실패한다")
            @Test
            void add_assignments_fails_if_no_more_round() {
                /*
                Given
                 */

                Long studyId = 1L;
                Long userId = 1L;
                AddAssignmentCommand addAssignmentCommand = AddAssignmentCommand.builder()
                    .build();

                when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(
                    eq(studyId), any(LocalDate.class)))
                    .thenReturn(
                        Optional.empty()
                    );

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.addAssignments(userId, studyId, addAssignmentCommand))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NEXT_ROUND_NOT_FOUND);
            }


            @DisplayName("스터디를 찾지못한경우 과제목록 추가에 실패한다")
            @Test
            void add_assignments_fails_if_study_not_found() {
                /*
                Given
                 */

                Long studyId = 1L;
                Long userId = 1L;
                AddAssignmentCommand addAssignmentCommand = AddAssignmentCommand.builder()
                    .build();

                when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(
                    eq(studyId), any(LocalDate.class)))
                    .thenReturn(
                        Optional.of(mock(Round.class))
                    );

                when(studyRepository.findWithLeaderById(
                    studyId))
                    .thenReturn(
                        Optional.empty()
                    );

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.addAssignments(userId, studyId, addAssignmentCommand))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);
            }
        }


        @Nested
        @DisplayName("과제삭제 테스트")
        class RemoveAssignment {

            @DisplayName("다음회차가 없는 경우 과제목록 삭제에 실패한다")
            @Test
            void remove_assignments_fails_if_no_more_round() {
                /*
                Given
                 */

                Long studyId = 1L;
                Long userId = 1L;
                DeleteAssignmentCommand deleteAssignmentCommand = DeleteAssignmentCommand.builder()
                    .build();

                when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(
                    eq(studyId), any(LocalDate.class)))
                    .thenReturn(
                        Optional.empty()
                    );

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.removeAssignments(userId, studyId,
                        deleteAssignmentCommand))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NEXT_ROUND_NOT_FOUND);
            }


            @DisplayName("스터디를 찾지못한경우 과제목록 삭제에 실패한다")
            @Test
            void remove_assignments_fails_if_study_not_found() {
                /*
                Given
                 */

                Long studyId = 1L;
                Long userId = 1L;
                DeleteAssignmentCommand deleteAssignmentCommand = DeleteAssignmentCommand.builder()
                    .build();

                when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(
                    eq(studyId), any(LocalDate.class)))
                    .thenReturn(
                        Optional.of(mock(Round.class))
                    );

                when(studyRepository.findWithLeaderById(
                    studyId))
                    .thenReturn(
                        Optional.empty()
                    );

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.removeAssignments(userId, studyId,
                        deleteAssignmentCommand))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);
            }


            @DisplayName("다음회차가 아닌 과제가 포함된경우 과제목록 삭제에 실패한다")
            @Test
            void remove_assignments_fails_if_contain_what_is_not_in_next_round() {
                /*
                Given
                 */

                Long studyId = 1L;
                Long userId = 2L;
                DeleteAssignmentCommand deleteAssignmentCommand = DeleteAssignmentCommand.builder()
                    .assignmentIds(List.of(3L))
                    .build();

                Round mockRound = mock(Round.class);

                when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(
                    eq(studyId), any(LocalDate.class)))
                    .thenReturn(
                        Optional.of(mockRound)
                    );

                when(studyRepository.findWithLeaderById(
                    studyId))
                    .thenReturn(
                        Optional.of(mock(Study.class))
                    );

                when(assignmentRepository.existsAllByIdInAndRoundNot(
                    eq(deleteAssignmentCommand.assignmentIds()),
                    eq(mockRound)))
                    .thenReturn(true);

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.removeAssignments(userId, studyId,
                        deleteAssignmentCommand))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_NEXT_ROUND_ASSIGNMENT);
            }
        }

        @Nested
        @DisplayName("과제수정 테스트")
        class EditAssignment {

            @DisplayName("다음회차가 없는 경우 과제목록 수정에 실패한다")
            @Test
            void edit_assignments_fails_if_no_more_round() {
                /*
                Given
                 */

                Long studyId = 1L;
                Long userId = 1L;
                EditAssignmentCommand editAssignmentCommand = EditAssignmentCommand.builder()
                    .build();

                when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(
                    eq(studyId), any(LocalDate.class)))
                    .thenReturn(
                        Optional.empty()
                    );

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.setAssignments(userId, studyId, editAssignmentCommand))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NEXT_ROUND_NOT_FOUND);
            }


            @DisplayName("스터디를 찾지못한경우 과제목록 수정에 실패한다")
            @Test
            void edit_assignments_fails_if_study_not_found() {
                /*
                Given
                 */

                Long studyId = 1L;
                Long userId = 1L;
                EditAssignmentCommand editAssignmentCommand = EditAssignmentCommand.builder()
                    .build();

                when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(
                    eq(studyId), any(LocalDate.class)))
                    .thenReturn(
                        Optional.of(mock(Round.class))
                    );

                when(studyRepository.findWithLeaderById(
                    studyId))
                    .thenReturn(
                        Optional.empty()
                    );

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.setAssignments(userId, studyId, editAssignmentCommand))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);
            }


            @DisplayName("요청에서 과제 ID가 중복되는 경우 과제목록 수정에 실패한다")
            @Test
            void edit_assignments_fails_if_assignment_duplicated_in_command() {
                /*
                Given
                 */

                Long studyId = 1L;
                Long userId = 2L;
                EditAssignmentCommand editAssignmentCommand = EditAssignmentCommand.builder()
                    .assignments(List.of(AssignmentInfo.builder()
                        .id(3L)
                        .build(), AssignmentInfo.builder()
                        .id(3L)
                        .build()))
                    .build();

                when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(
                    eq(studyId), any(LocalDate.class)))
                    .thenReturn(
                        Optional.of(mock(Round.class))
                    );

                when(studyRepository.findWithLeaderById(
                    studyId))
                    .thenReturn(
                        Optional.of(mock(Study.class))
                    );

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.setAssignments(userId, studyId, editAssignmentCommand))
                    .isInstanceOf(DuplicationException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_ASSIGNMENT_ID);
            }


            @DisplayName("과제 하나라도 찾을 수 없다면 과제목록 수정에 실패한다")
            @Test
            void edit_assignments_fails_if_whichever_is_not_found() {
                /*
                Given
                 */

                Long studyId = 1L;
                Long userId = 2L;
                EditAssignmentCommand editAssignmentCommand = EditAssignmentCommand.builder()
                    .assignments(List.of(AssignmentInfo.builder()
                        .id(3L)
                        .build(), AssignmentInfo.builder()
                        .id(4L)
                        .build()))
                    .build();

                when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(
                    eq(studyId), any(LocalDate.class)))
                    .thenReturn(
                        Optional.of(mock(Round.class))
                    );

                when(studyRepository.findWithLeaderById(studyId))
                    .thenReturn(
                        Optional.of(mock(Study.class))
                    );

                when(assignmentRepository.findAllById(
                    editAssignmentCommand.assignments().stream().map(AssignmentInfo::id)
                        .collect(Collectors.toSet())))
                    .thenReturn(
                        List.of(mock(Assignment.class))
                    );

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.setAssignments(userId, studyId, editAssignmentCommand))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ASSIGNMENT_NOT_FOUND);
            }

            @DisplayName("다음회차가 아닌 과제가 포함돼있다면 과제목록 수정에 실패한다")
            @Test
            void edit_assignments_fails_if_whichever_is_not_next_round() {
                /*
                Given
                 */

                Long studyId = 1L;
                Long userId = 2L;
                EditAssignmentCommand editAssignmentCommand = EditAssignmentCommand.builder()
                    .assignments(List.of(AssignmentInfo.builder()
                        .id(3L)
                        .build(), AssignmentInfo.builder()
                        .id(4L)
                        .build()))
                    .build();

                when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(
                    eq(studyId), any(LocalDate.class)))
                    .thenReturn(
                        Optional.of(mock(Round.class))
                    );

                when(studyRepository.findWithLeaderById(studyId))
                    .thenReturn(
                        Optional.of(mock(Study.class))
                    );

                when(assignmentRepository.findAllById(
                    editAssignmentCommand.assignments().stream().map(AssignmentInfo::id)
                        .collect(Collectors.toSet())))
                    .thenReturn(
                        editAssignmentCommand.assignments().stream().map(
                            info -> Assignment.builder()
                                .id(info.id())
                                .round(mock(Round.class))
                                .build()
                        ).collect(Collectors.toList())
                    );

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.setAssignments(userId, studyId, editAssignmentCommand))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_NEXT_ROUND_ASSIGNMENT);
            }
        }

        @Nested
        @DisplayName("과제조회 테스트")
        class GetAssignment {

            @DisplayName("라운드를 찾지 못한 경우 과제목록 조회에 실패한다")
            @Test
            void get_assignments_fails_if_round_not_found() {
                /*
                Given
                 */

                Long studyId = 1L;
                Integer roundIdx = 2;

                when(roundRepository.findRoundByStudyAndIdx(
                    eq(studyId), eq(roundIdx)))
                    .thenReturn(
                        Optional.empty()
                    );

                /*
                When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.getAssignments(studyId, roundIdx))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROUND_NOT_FOUND);
            }
        }
    }

    @Nested
    @DisplayName("투표 집계 테스트")
    class CountingVoteTest {

        @Nested
        @DisplayName("중복할당 불가 투표집계")
        class CountingVoteWithoutDuplication {

            @Test
            @DisplayName("멤버가 아닌 유저가 한 투표가 있는 경우 투표 집계는 실패한다.")
            void counting_vote_fail_if_vote_by_not_member() {
                /*
                 * Given
                 */
                Study study = BookStudy.builder()
                    .id(1L)
                    .build();

                Round round = mock(Round.class);

                List<Assignment> assignments = List.of(Assignment.builder().id(2L).build());

                List<User> members = List.of();
                List<UserStudy> userStudies = members.stream().map(
                    member -> UserStudy.builder()
                        .user(member)
                        .study(study)
                        .build()
                ).collect(Collectors.toList());

                List<AssignmentVote> votes = List.of(
                    AssignmentVote.builder()
                        .first(Assignment.builder().id(assignments.getFirst().getId()).build())
                        .user(mock(User.class))
                        .build()
                );

                when(assignmentRepository.findAllByRound(round)).thenReturn(assignments);
                when(userStudyRepository.findWithUserByStudyId(study.getId())).thenReturn(
                    userStudies);
                when(assignmentVoteRepository.findAllByRound(round)).thenReturn(votes);

                /*
                 * When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.startRound(study, round))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_VOTE_BY_NOT_MEMBER);
            }


            @Test
            @DisplayName("첫번째 선택이 모르는 과제인 투표가 있는 경우 투표 집계는 실패한다.")
            void counting_vote_fail_if_vote_for_unknown_assignment() {
                /*
                 * Given
                 */
                Study study = BookStudy.builder()
                    .id(1L)
                    .build();

                Round round = mock(Round.class);

                List<Assignment> assignments = List.of(Assignment.builder().id(2L).build());

                List<User> members = List.of(User.builder().id(3L).build());
                List<UserStudy> userStudies = members.stream().map(
                    member -> UserStudy.builder()
                        .user(member)
                        .study(study)
                        .build()
                ).collect(Collectors.toList());

                List<AssignmentVote> votes = List.of(
                    AssignmentVote.builder()
                        .first(Assignment.builder().id(assignments.getFirst().getId() + 1).build())
                        .user(members.getFirst())
                        .build()
                );

                when(assignmentRepository.findAllByRound(round)).thenReturn(assignments);
                when(userStudyRepository.findWithUserByStudyId(study.getId())).thenReturn(
                    userStudies);
                when(assignmentVoteRepository.findAllByRound(round)).thenReturn(votes);

                /*
                 * When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.startRound(study, round))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode",
                        ErrorCode.INVALID_VOTE_FOR_UNKNOWN_ASSIGNMENT);
            }

            @Test
            @DisplayName("두번째 선택이 모르는 과제인 투표가 있는 경우 투표 집계는 실패한다.")
            void counting_vote_fail_if_second_vote_for_unknown_assignment() {
                /*
                 * Given
                 */
                AtomicLong id = new AtomicLong(1);
                Study study = BookStudy.builder()
                    .id(id.addAndGet(1))
                    .build();

                Round round = mock(Round.class);

                List<Assignment> assignments = List.of(
                    Assignment.builder().id(id.addAndGet(1)).build(),
                    Assignment.builder().id(id.addAndGet(1)).build()
                );

                List<User> members = List.of(User.builder().id(id.addAndGet(1)).build());
                List<UserStudy> userStudies = members.stream().map(
                    member -> UserStudy.builder()
                        .user(member)
                        .study(study)
                        .build()
                ).collect(Collectors.toList());

                List<AssignmentVote> votes = List.of(
                    AssignmentVote.builder()
                        .first(assignments.getFirst())
                        .second(Assignment.builder().id(id.addAndGet(1)).build())
                        .user(members.getFirst())
                        .build()
                );

                when(assignmentRepository.findAllByRound(round)).thenReturn(assignments);
                when(userStudyRepository.findWithUserByStudyId(study.getId())).thenReturn(
                    userStudies);
                when(assignmentVoteRepository.findAllByRound(round)).thenReturn(votes);

                /*
                 * When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.startRound(study, round))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode",
                        ErrorCode.INVALID_VOTE_FOR_UNKNOWN_ASSIGNMENT);
            }

            @Test
            @DisplayName("중복 투표가 있는 경우 투표 집계는 실패한다.")
            void counting_vote_fail_if_multiple_vote_by_anyone() {
                /*
                 * Given
                 */
                AtomicLong id = new AtomicLong(1);
                Study study = BookStudy.builder()
                    .id(id.addAndGet(1))
                    .build();

                Round round = mock(Round.class);

                List<Assignment> assignments = List.of(
                    Assignment.builder().id(id.addAndGet(1)).build(),
                    Assignment.builder().id(id.addAndGet(1)).build()
                );

                List<User> members = List.of(User.builder().id(id.addAndGet(1)).build());
                List<UserStudy> userStudies = members.stream().map(
                    member -> UserStudy.builder()
                        .user(member)
                        .study(study)
                        .build()
                ).collect(Collectors.toList());

                List<AssignmentVote> votes = List.of(
                    AssignmentVote.builder()
                        .first(assignments.getFirst())
                        .second(assignments.getLast())
                        .user(members.getFirst())
                        .build(),
                    AssignmentVote.builder()
                        .first(assignments.getFirst())
                        .user(members.getFirst())
                        .build()
                );

                when(assignmentRepository.findAllByRound(round)).thenReturn(assignments);
                when(userStudyRepository.findWithUserByStudyId(study.getId())).thenReturn(
                    userStudies);
                when(assignmentVoteRepository.findAllByRound(round)).thenReturn(votes);

                /*
                 * When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.startRound(study, round))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode",
                        ErrorCode.MULTIPLE_VOTE);
            }
        }

        @Nested
        @DisplayName("중복할당 가능 투표집계")
        class CountingVoteWithDuplication {

            @Test
            @DisplayName("멤버가 아닌 유저가 한 투표가 있는 경우 투표 집계는 실패한다.")
            void counting_vote_fail_if_vote_by_not_member() {
                /*
                 * Given
                 */
                Study study = BookStudy.builder()
                    .id(1L)
                    .duplicated(true)
                    .build();

                Round round = mock(Round.class);

                List<Assignment> assignments = List.of(Assignment.builder().id(2L).build());

                List<User> members = List.of();
                List<UserStudy> userStudies = members.stream().map(
                    member -> UserStudy.builder()
                        .user(member)
                        .study(study)
                        .build()
                ).collect(Collectors.toList());

                List<AssignmentVote> votes = List.of(
                    AssignmentVote.builder()
                        .first(Assignment.builder().id(assignments.getFirst().getId()).build())
                        .user(mock(User.class))
                        .build()
                );

                when(assignmentRepository.findAllByRound(round)).thenReturn(assignments);
                when(userStudyRepository.findWithUserByStudyId(study.getId())).thenReturn(
                    userStudies);
                when(assignmentVoteRepository.findAllByRound(round)).thenReturn(votes);

                /*
                 * When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.startRound(study, round))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_VOTE_BY_NOT_MEMBER);
            }

            @Test
            @DisplayName("모르는 과제에 대한 투표가 있는경우 투표 집계는 실패한다.")
            void counting_vote_fail_if_vote_for_unknown_assignment() {
                /*
                 * Given
                 */
                AtomicLong id = new AtomicLong(0);
                Study study = BookStudy.builder()
                    .id(id.addAndGet(1))
                    .duplicated(true)
                    .build();

                Round round = mock(Round.class);

                List<Assignment> assignments = List.of(
                    Assignment.builder().id(id.addAndGet(1)).build());

                List<User> members = List.of(User.builder().id(id.addAndGet(1)).build());
                List<UserStudy> userStudies = members.stream().map(
                    member -> UserStudy.builder()
                        .user(member)
                        .study(study)
                        .build()
                ).collect(Collectors.toList());

                List<AssignmentVote> votes = List.of(
                    AssignmentVote.builder()
                        .first(Assignment.builder().id(id.addAndGet(1)).build())
                        .user(members.getFirst())
                        .build()
                );

                when(assignmentRepository.findAllByRound(round)).thenReturn(assignments);
                when(userStudyRepository.findWithUserByStudyId(study.getId())).thenReturn(
                    userStudies);
                when(assignmentVoteRepository.findAllByRound(round)).thenReturn(votes);

                /*
                 * When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.startRound(study, round))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode",
                        ErrorCode.INVALID_VOTE_FOR_UNKNOWN_ASSIGNMENT);
            }


            @Test
            @DisplayName("중복 투표가 있는경우 투표 집계는 실패한다.")
            void counting_vote_fail_if_multiple_vote() {
                /*
                 * Given
                 */
                AtomicLong id = new AtomicLong(0);
                Study study = BookStudy.builder()
                    .id(id.addAndGet(1))
                    .duplicated(true)
                    .build();

                Round round = mock(Round.class);

                List<Assignment> assignments = List.of(
                    Assignment.builder().id(id.addAndGet(1)).build(),
                    Assignment.builder().id(id.addAndGet(1)).build());

                List<User> members = List.of(User.builder().id(id.addAndGet(1)).build());
                List<UserStudy> userStudies = members.stream().map(
                    member -> UserStudy.builder()
                        .user(member)
                        .study(study)
                        .build()
                ).collect(Collectors.toList());

                List<AssignmentVote> votes = List.of(
                    AssignmentVote.builder()
                        .first(assignments.getFirst())
                        .user(members.getFirst())
                        .build(),
                    AssignmentVote.builder()
                        .first(assignments.getLast())
                        .user(members.getFirst())
                        .build()
                );

                when(assignmentRepository.findAllByRound(round)).thenReturn(assignments);
                when(userStudyRepository.findWithUserByStudyId(study.getId())).thenReturn(
                    userStudies);
                when(assignmentVoteRepository.findAllByRound(round)).thenReturn(votes);

                /*
                 * When & Then
                 */
                assertThatThrownBy(
                    () -> bookStudyService.startRound(study, round))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode",
                        ErrorCode.MULTIPLE_VOTE);
            }
        }
    }

    @Nested
    @DisplayName("투표 테스트")
    class VoteTest {


        @DisplayName("스터디를 찾지못한 경우 과제 투표는 실패한다.")
        @Test
        void vote_assignment_fail_if_study_not_found() {
            /*
             * Given
             */
            Long userId = 1L;
            Long studyId = 2L;
            VoteAssignmentCommand voteAssignmentCommand = VoteAssignmentCommand.builder().build();

            when(studyRepository.findById(studyId)).thenReturn(Optional.empty());


            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> bookStudyService.voteAssignment(userId, studyId, voteAssignmentCommand))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);
        }

        @DisplayName("멤버가 아닌경우 과제 투표는 실패한다.")
        @Test
        void vote_assignment_fail_if_not_member() {
            /*
             * Given
             */
            Long userId = 1L;
            Long studyId = 2L;
            VoteAssignmentCommand voteAssignmentCommand = VoteAssignmentCommand.builder().build();

            when(userRepository.findById(userId))
                .thenReturn(Optional.of(mock(User.class)));
            when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(mock(Study.class)));
            when(userStudyRepository.existsByUserIdAndStudyId(userId, studyId))
                .thenReturn(false);


            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> bookStudyService.voteAssignment(userId, studyId, voteAssignmentCommand))
                .isInstanceOf(ForbiddenException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ONLY_MEMBER_ALLOWED);
        }

        @DisplayName("다음회차를 찾지못한 경우 과제 투표는 실패한다.")
        @Test
        void vote_assignment_fail_if_no_more_round() {
            /*
             * Given
             */
            Long userId = 1L;
            Long studyId = 2L;
            VoteAssignmentCommand voteAssignmentCommand = VoteAssignmentCommand.builder()
                .build();

            when(userRepository.findById(userId))
                .thenReturn(Optional.of(mock(User.class)));
            when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(mock(Study.class)));
            when(userStudyRepository.existsByUserIdAndStudyId(userId, studyId))
                .thenReturn(true);

            when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(eq(studyId), any(
                LocalDate.class)))
                .thenReturn(Optional.empty());

            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> bookStudyService.voteAssignment(userId, studyId, voteAssignmentCommand))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NEXT_ROUND_NOT_FOUND);
        }

        @DisplayName("1순위로 투표한 과제를 찾지못한 경우 과제 투표는 실패한다.")
        @Test
        void vote_assignment_fail_if_first_assignment_not_found() {
            /*
             * Given
             */
            Long userId = 1L;
            Long studyId = 2L;
            VoteAssignmentCommand voteAssignmentCommand = VoteAssignmentCommand.builder()
                .first(3L)
                .second(4L)
                .build();

            when(userRepository.findById(userId))
                .thenReturn(Optional.of(mock(User.class)));
            when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(mock(Study.class)));
            when(userStudyRepository.existsByUserIdAndStudyId(userId, studyId))
                .thenReturn(true);

            when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(eq(studyId), any(
                LocalDate.class)))
                .thenReturn(Optional.of(mock(Round.class)));

            when(assignmentRepository.findById(voteAssignmentCommand.first()))
                .thenReturn(Optional.empty());
            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> bookStudyService.voteAssignment(userId, studyId, voteAssignmentCommand))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ASSIGNMENT_NOT_FOUND);
        }

        @DisplayName("1순위로 투표한 과제가 다음회차가 아닌 경우 과제 투표는 실패한다.")
        @Test
        void vote_assignment_fail_if_first_assignment_not_next_round() {
            /*
             * Given
             */
            Long userId = 1L;
            Long studyId = 2L;
            VoteAssignmentCommand voteAssignmentCommand = VoteAssignmentCommand.builder()
                .first(3L)
                .second(4L)
                .build();
            when(userRepository.findById(userId))
                .thenReturn(Optional.of(mock(User.class)));
            when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(mock(Study.class)));
            when(userStudyRepository.existsByUserIdAndStudyId(userId, studyId))
                .thenReturn(true);

            when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(eq(studyId), any(
                LocalDate.class)))
                .thenReturn(Optional.of(mock(Round.class)));

            when(assignmentRepository.findById(voteAssignmentCommand.first()))
                .thenReturn(Optional.of(
                    Assignment.builder()
                        .round(mock(Round.class))
                        .build()
                ));
            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> bookStudyService.voteAssignment(userId, studyId, voteAssignmentCommand))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_NEXT_ROUND_ASSIGNMENT);
        }


        @DisplayName("2순위로 투표한 과제를 찾지못한경우 과제 투표는 실패한다.")
        @Test
        void vote_assignment_fail_if_second_assignment_not_found() {
            /*
             * Given
             */
            Long userId = 1L;
            Long studyId = 2L;
            VoteAssignmentCommand voteAssignmentCommand = VoteAssignmentCommand.builder()
                .first(3L)
                .second(4L)
                .build();

            Round nextRound = mock(Round.class);
            when(userRepository.findById(userId))
                .thenReturn(Optional.of(mock(User.class)));
            when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(mock(Study.class)));
            when(userStudyRepository.existsByUserIdAndStudyId(userId, studyId))
                .thenReturn(true);

            when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(eq(studyId), any(
                LocalDate.class)))
                .thenReturn(Optional.of(nextRound));

            when(assignmentRepository.findById(voteAssignmentCommand.first()))
                .thenReturn(Optional.of(
                    Assignment.builder()
                        .round(nextRound)
                        .build()
                ));

            when(assignmentRepository.findById(voteAssignmentCommand.second()))
                .thenReturn(Optional.empty()
                );
            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> bookStudyService.voteAssignment(userId, studyId, voteAssignmentCommand))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ASSIGNMENT_NOT_FOUND);
        }

        @DisplayName("2순위로 투표한 과제가 다음회차가 아닌경우 과제 투표는 실패한다.")
        @Test
        void vote_assignment_fail_if_second_assignment_not_next_round() {
            /*
             * Given
             */
            Long userId = 1L;
            Long studyId = 2L;
            VoteAssignmentCommand voteAssignmentCommand = VoteAssignmentCommand.builder()
                .first(3L)
                .second(4L)
                .build();

            Round nextRound = mock(Round.class);
            when(userRepository.findById(userId))
                .thenReturn(Optional.of(mock(User.class)));
            when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(mock(Study.class)));
            when(userStudyRepository.existsByUserIdAndStudyId(userId, studyId))
                .thenReturn(true);

            when(roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(eq(studyId), any(
                LocalDate.class)))
                .thenReturn(Optional.of(nextRound));

            when(assignmentRepository.findById(voteAssignmentCommand.first()))
                .thenReturn(Optional.of(
                    Assignment.builder()
                        .round(nextRound)
                        .build()
                ));

            when(assignmentRepository.findById(voteAssignmentCommand.second()))
                .thenReturn(Optional.of(
                        Assignment.builder()
                            .round(mock(Round.class))
                            .build()
                    )
                );
            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> bookStudyService.voteAssignment(userId, studyId, voteAssignmentCommand))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_NEXT_ROUND_ASSIGNMENT);
        }

        @DisplayName("유저를 찾지못한경우 과제 투표는 실패한다.")
        @Test
        void vote_assignment_fail_if_user_not_found() {
            /*
             * Given
             */
            Long userId = 1L;
            Long studyId = 2L;
            VoteAssignmentCommand voteAssignmentCommand = VoteAssignmentCommand.builder()
                .first(3L)
                .second(4L)
                .build();

            Round nextRound = mock(Round.class);
            when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(mock(Study.class)));
            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> bookStudyService.voteAssignment(userId, studyId, voteAssignmentCommand))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }

        @DisplayName("투표 시작 테스트")
        @Nested
        class VoteStartTest {

            @DisplayName("스터디를 찾지못한경우 투표 시작은 실패한다")
            @Test
            void start_vote_fail_if_study_not_found() {
                /*
                 * Given
                 */
                Long userId = 1L;
                Long studyId = 2L;

                when(studyRepository.findWithLeaderById(studyId))
                    .thenReturn(Optional.empty());
                /*
                 * When & Then
                 */

                assertThatThrownBy(
                    () -> studyService.startVoting(userId, studyId))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);
            }
        }

    }

    @Nested
    @DisplayName("스터디 설정 테스트")
    class StudyConfigureTest {

        @DisplayName("스터디를 찾지못한경우 스터디 설정은 실패한다")
        @Test
        void configure_study_fail_if_study_not_found() {
            /*
             * Given
             */
            Long userId = 1L;
            Long studyId = 2L;
            ConfigureStudyCommand configureStudyCommand = ConfigureStudyCommand.builder()
                .duplicated(true)
                .build();

            /*
             * When & Then
             */
            assertThatThrownBy(
                () -> studyService.configure(userId, studyId, configureStudyCommand))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_NOT_FOUND);
        }
    }
}