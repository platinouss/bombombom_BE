package com.bombombom.devs.external.algo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.exception.NotAcceptableException;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.external.algo.config.ProbabilityConfig;
import com.bombombom.devs.external.algo.service.dto.command.FeedbackAlgorithmProblemCommand;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.BookStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.StudyStatus;
import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AlgorithmProblemServiceTest {

    @Mock
    private RandomGenerator randomGenerator;

    @InjectMocks
    private AlgorithmProblemService algorithmProblemService;

    @Mock
    AlgorithmProblemRepository algorithmProblemRepository;
    @Mock
    UserStudyRepository userStudyRepository;
    @Mock
    AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;
    @Mock
    StudyRepository studyRepository;
    @Mock
    RoundRepository roundRepository;

    @Mock
    Clock clock;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("각 태그마다 정해진 추첨 스프레드를 가지고 해당 스프레드는 누적합을 통해 계산된다.")
    void drawProblem() {
        /*
         * Given
         */
        final int totalProblemCount = 1;
        ProbabilityConfig.totalProbability = 1.0;
        ProbabilityConfig config = new ProbabilityConfig();
        ReflectionTestUtils.setField(config, "math", 1.0);
        ReflectionTestUtils.setField(config, "dp", 1.0);
        ReflectionTestUtils.setField(config, "greedy", 1.0);
        ReflectionTestUtils.setField(config, "impl", 1.0);
        ReflectionTestUtils.setField(config, "graph", 1.0);
        ReflectionTestUtils.setField(config, "geometry", 1.0);
        ReflectionTestUtils.setField(config, "ds", 1.0);
        ReflectionTestUtils.setField(config, "string", 1.0);
        config.init();


        /*
         * When (Random Double = 0.0)
         */
        when(randomGenerator.nextDouble(anyDouble())).thenReturn(0.0);
        Map<AlgoTag, Integer> result0 =
            algorithmProblemService.getProblemCountForEachTag(totalProblemCount);

        /*
         * Then
         */
        assertThat(result0).isNotNull();
        assertThat(result0.get(AlgoTag.MATH)).isEqualTo(totalProblemCount);

        /*
         * When (Random Double = 1.0)
         */
        when(randomGenerator.nextDouble(anyDouble())).thenReturn(1.0);
        Map<AlgoTag, Integer> result1 =
            algorithmProblemService.getProblemCountForEachTag(totalProblemCount);

        /*
         * Then
         */
        assertThat(result1).isNotNull();
        assertThat(result1.get(AlgoTag.DP)).isEqualTo(totalProblemCount);

        /*
         * When (Random Double = 2.0)
         */
        when(randomGenerator.nextDouble(anyDouble())).thenReturn(2.0);
        Map<AlgoTag, Integer> result2 =
            algorithmProblemService.getProblemCountForEachTag(totalProblemCount);

        /*
         * Then
         */
        assertThat(result2).isNotNull();
        assertThat(result2.get(AlgoTag.GREEDY)).isEqualTo(totalProblemCount);

        /*
         * When (Random Double = 3.0)
         */
        when(randomGenerator.nextDouble(anyDouble())).thenReturn(3.0);
        Map<AlgoTag, Integer> result3 =
            algorithmProblemService.getProblemCountForEachTag(totalProblemCount);

        /*
         * Then
         */
        assertThat(result3).isNotNull();
        assertThat(result3.get(AlgoTag.IMPLEMENTATION)).isEqualTo(totalProblemCount);

        /*
         * When (Random Double = 4.0)
         */
        when(randomGenerator.nextDouble(anyDouble())).thenReturn(4.0);
        Map<AlgoTag, Integer> result4 =
            algorithmProblemService.getProblemCountForEachTag(totalProblemCount);

        /*
         * Then
         */
        assertThat(result4).isNotNull();
        assertThat(result4.get(AlgoTag.GRAPHS)).isEqualTo(totalProblemCount);

        /*
         * When (Random Double = 5.0)
         */
        when(randomGenerator.nextDouble(anyDouble())).thenReturn(5.0);
        Map<AlgoTag, Integer> result5 =
            algorithmProblemService.getProblemCountForEachTag(totalProblemCount);

        /*
         * Then
         */
        assertThat(result5).isNotNull();
        assertThat(result5.get(AlgoTag.GEOMETRY)).isEqualTo(totalProblemCount);

        /*
         * When (Random Double = 6.0)
         */
        when(randomGenerator.nextDouble(anyDouble())).thenReturn(6.0);
        Map<AlgoTag, Integer> result6 =
            algorithmProblemService.getProblemCountForEachTag(totalProblemCount);

        /*
         * Then
         */
        assertThat(result6).isNotNull();
        assertThat(result6.get(AlgoTag.DATA_STRUCTURES)).isEqualTo(totalProblemCount);

        /*
         * When (Random Double = 7.0)
         */
        when(randomGenerator.nextDouble(anyDouble())).thenReturn(7.0);
        Map<AlgoTag, Integer> result7 =
            algorithmProblemService.getProblemCountForEachTag(totalProblemCount);

        /*
         * Then
         */
        assertThat(result7).isNotNull();
        assertThat(result7.get(AlgoTag.STRING)).isEqualTo(totalProblemCount);
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
        assertThatThrownBy(() -> algorithmProblemService.feedback(
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
        assertThatThrownBy(() -> algorithmProblemService.feedback(
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
        assertThatThrownBy(() -> algorithmProblemService.feedback(
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
        assertThatThrownBy(() -> algorithmProblemService.feedback(
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
        /*
         * When & Then
         */
        assertThatThrownBy(() -> algorithmProblemService.feedback(
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

        when(userStudyRepository.existsByUserIdAndStudyId(
            testuser.getId(), study.getId()
        )).thenReturn(false);
        /*
         * When & Then
         */
        assertThatThrownBy(() -> algorithmProblemService.feedback(
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


        /*
         * When & Then
         */
        assertThatThrownBy(() -> algorithmProblemService.feedback(
            testuser.getId(), feedback))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("User Not Found");


    }
}