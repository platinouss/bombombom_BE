package com.bombombom.devs.external.study.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.bombombom.devs.core.exception.NotAcceptableException;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.SystemClock;
import com.bombombom.devs.external.algo.service.dto.command.FeedbackAlgorithmProblemCommand;
import com.bombombom.devs.external.study.service.dto.command.JoinStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.RegisterBookStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.StartStudyCommand;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmProblemResult;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmProblemSolveHistoryResult;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.external.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.external.study.service.dto.result.RoundResult;
import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.StudyResult;
import com.bombombom.devs.external.study.service.dto.result.progress.AlgorithmStudyProgress;
import com.bombombom.devs.external.study.service.factory.StudyServiceFactory;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import com.bombombom.devs.study.model.AlgorithmProblemSolveHistory;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.AlgorithmStudyDifficulty;
import com.bombombom.devs.study.model.BookStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.StudyStatus;
import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.study.model.UserStudy;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.AlgorithmProblemSolveHistoryRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
    AlgorithmProblemRepository algorithmProblemRepository;

    @Mock
    private AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;

    @Mock
    private AlgorithmProblemSolveHistoryRepository algorithmProblemSolveHistoryRepository;

    @InjectMocks
    private AlgorithmStudyService algorithmStudyService;

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
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Already Joined Study");
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

        when(userRepository.findById(1L)).thenReturn(Optional.of(testuser));


        /*
        When
         */
        AlgorithmStudyResult algorithmStudyResult = studyService.createAlgorithmStudy(
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
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testuser));
        when(bookRepository.findByIsbn(123456789L)).thenReturn(Optional.of(book));

        /*
        When
         */
        BookStudyResult bookStudyResult = studyService.createBookStudy(
            testuser.getId(),
            registerBookStudyCommand);

        /*
        Then
         */
        StudyResult expectedResponse = StudyResult.fromEntity(
            bookStudy);

        Assertions.assertThat(bookStudyResult).isEqualTo(expectedResponse);
    }


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
            .hasMessage("Problem Not Found");

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
            .hasMessage("Study Not Found");

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
            .isInstanceOf(NotAcceptableException.class)
            .hasMessage("Feedback can only be given to Algorithm Study");

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
            .hasMessage("Ongoing Round Not Found");

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

        AlgorithmProblemSolveHistory history = mock(AlgorithmProblemSolveHistory.class);

        LocalDateTime now = LocalDateTime.now();

        when(history.getSolvedAt()).thenReturn(now);
        when(algorithmProblemSolveHistoryRepository.findByUserIdAndProblemId(
            1L, algorithmProblem.getId()
        )).thenReturn(Optional.of(history));
        /*
         * When & Then
         */
        assertThatThrownBy(() -> algorithmStudyService.feedback(
            1L, feedback))
            .isInstanceOf(NotAcceptableException.class)
            .hasMessage("Problem is not ongoing assignment");

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

        AlgorithmProblemSolveHistory history = mock(AlgorithmProblemSolveHistory.class);

        LocalDateTime now = LocalDateTime.now();

        when(history.getSolvedAt()).thenReturn(now);
        when(algorithmProblemSolveHistoryRepository.findByUserIdAndProblemId(
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
            .isInstanceOf(NotAcceptableException.class)
            .hasMessage("User is not a member");

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

        AlgorithmProblemSolveHistory history = mock(AlgorithmProblemSolveHistory.class);

        LocalDateTime now = LocalDateTime.now();

        when(history.getSolvedAt()).thenReturn(now);
        when(algorithmProblemSolveHistoryRepository.findByUserIdAndProblemId(
            testuser.getId(), algorithmProblem.getId()
        )).thenReturn(Optional.of(history));
        /*
         * When & Then
         */
        assertThatThrownBy(() -> algorithmStudyService.feedback(
            testuser.getId(), feedback))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("User Not Found");


    }


    @Test
    @DisplayName("유저의 문제 풀이 기록을 찾을 수 없으면 실패한다")
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

        AlgorithmProblemSolveHistory history = mock(AlgorithmProblemSolveHistory.class);

        LocalDateTime now = LocalDateTime.now();

        when(algorithmProblemSolveHistoryRepository.findByUserIdAndProblemId(
            userId, problem.getId()
        )).thenReturn(Optional.empty());

        /*
         * When & Then
         */
        assertThatThrownBy(() -> algorithmStudyService.feedback(
            userId, feedback))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Solve History Not Found");

    }


    @Test
    @DisplayName("유저가 문제를 풀지 않았다면 실패한다")
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

        AlgorithmProblemSolveHistory history = mock(AlgorithmProblemSolveHistory.class);

        LocalDateTime now = LocalDateTime.now();

        when(algorithmProblemSolveHistoryRepository.findByUserIdAndProblemId(
            userId, problem.getId()
        )).thenReturn(Optional.of(history));

        /*
         * When & Then
         */
        assertThatThrownBy(() -> algorithmStudyService.feedback(
            userId, feedback))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cant Give Feedback On Unsolved Problem");

    }

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
        AlgorithmProblemSolveHistory history = AlgorithmProblemSolveHistory.builder()
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
        doReturn(members).when(userStudyRepository).findByStudyId(anyLong());
        doReturn(algorithmStudyService).when(studyServiceFactory).getService(StudyType.ALGORITHM);
        doReturn(List.of(assignment1, assignment2)).when(algorithmProblemAssignmentRepository)
            .findAssignmentWithProblemByRoundId(anyLong());
        doReturn(List.of(history)).when(algorithmProblemSolveHistoryRepository)
            .findSolvedHistoryWithUserAndProblem(anyList(), anyList());

        /*
        When & Then
         */
        Assertions.assertThat(studyService.findStudyProgress(studyId, roundIdx))
            .isEqualTo(studyProgressResult);
    }

    @DisplayName("존재하지 않는 알고리즘 스터디인 경우 진행 현황 조회에 실패한다.")
    @Test
    void retrieve_algorithm_study_progress_with_non_existent_study_fail() {
        /*
        Given
         */
        Long studyId = 1L;
        Integer roundIdx = 1;

        doReturn(Optional.empty()).when(studyRepository).findById(anyLong());

        /*
        When & Then
         */
        assertThrows(IllegalStateException.class,
            () -> studyService.findStudyProgress(studyId, roundIdx));
    }

    @DisplayName("해당 회차 정보가 존재하지 않는 경우 알고리즘 스터디 진행 현황 조회에 실패한다.")
    @Test
    void retrieve_algorithm_study_progress_with_non_existent_round_fail() {
        /*
        Given
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

        doReturn(Optional.of(study)).when(studyRepository).findById(anyLong());
        doReturn(Optional.empty()).when(roundRepository)
            .findRoundByStudyAndIdx(anyLong(), anyInt());

        /*
        When & Then
         */
        assertThrows(IllegalStateException.class,
            () -> studyService.findStudyProgress(studyId, roundIdx));
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
        AlgorithmProblemSolveHistory history = AlgorithmProblemSolveHistory.builder()
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
        StudyDetailsResult studyDetailsResult = StudyDetailsResult.builder()
            .studyType(StudyType.ALGORITHM)
            .name("스터디")
            .introduce("안녕하세요")
            .headCount(1)
            .capacity(5)
            .penalty(10000)
            .reliabilityLimit(0)
            .startDate(roundStartDate)
            .weeks(2)
            .leaderId(1L)
            .status(StudyStatus.RUNNING)
            .currentStudyProgress(studyProgressResult)
            .build();

        doReturn(Optional.of(study)).when(studyRepository).findById(anyLong());
        doReturn(Optional.of(round)).when(roundRepository)
            .findRoundByStudyIdAndBetweenStartDateAndEndDateOrIdx(anyLong(), anyInt(),
                any(LocalDate.class));
        doReturn(members).when(userStudyRepository).findByStudyId(anyLong());
        doReturn(algorithmStudyService).when(studyServiceFactory).getService(StudyType.ALGORITHM);
        doReturn(List.of(assignment1, assignment2)).when(algorithmProblemAssignmentRepository)
            .findAssignmentWithProblemByRoundId(anyLong());
        doReturn(List.of(history)).when(algorithmProblemSolveHistoryRepository)
            .findSolvedHistoryWithUserAndProblem(anyList(), anyList());

        /*
        When & Then
         */
        Assertions.assertThat(studyService.findStudyDetails(studyId)).isEqualTo(studyDetailsResult);
    }

    @DisplayName("존재하지 않는 알고리즘 스터디인 경우 스터디 정보 조회에 실패한다.")
    @Test
    void find_study_details_with_non_existent_study_fail() {
        /*
        Given
         */
        Long studyId = 1L;

        doReturn(Optional.empty()).when(studyRepository).findById(anyLong());

        /*
        When & Then
         */
        assertThrows(IllegalStateException.class, () -> studyService.findStudyDetails(studyId));
    }

    @DisplayName("회차 정보가 존재하지 않는 경우 알고리즘 스터디 정보 조회에 실패한다.")
    @Test
    void retrieve_algorithm_study_details_with_non_existent_round_fail() {
        /*
        Given
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

        doReturn(Optional.of(study)).when(studyRepository).findById(anyLong());
        doReturn(Optional.empty()).when(roundRepository)
            .findRoundByStudyIdAndBetweenStartDateAndEndDateOrIdx(anyLong(), anyInt(),
                any(LocalDate.class));

        /*
        When & Then
         */
        assertThrows(IllegalStateException.class, () -> studyService.findStudyDetails(studyId));
    }

    @DisplayName("스터디 시작은 스터디를 찾지 못한 경우 실패한다")
    @Test
    void start_study_fails_if_study_not_found() {
        /*
        Given
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
        When & Then
         */
        assertThatThrownBy(() -> studyService.start(userId, startStudyCommand))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Study Not Found");


    }

    @DisplayName("스터디 시작은 리더가 아닌 경우 실패한다")
    @Test
    void start_study_fails_if_user_is_not_leader() {
        /*
        Given
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
        When & Then
         */
        assertThatThrownBy(() -> studyService.start(userId, startStudyCommand))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Only Leader Can Start Study");


    }

    @DisplayName("스터디가 이미 시작한 경우 스터디 시작은 실패한다")
    @Test
    void start_study_fails_if_study_already_started() {
        /*
        Given
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
        When & Then
         */
        assertThatThrownBy(() -> studyService.start(userId, startStudyCommand))
            .isInstanceOf(NotAcceptableException.class)
            .hasMessage("Study Already Started");


    }
}