package com.bombombom.devs.external.study.service;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.algo.model.AlgorithmProblemFeedback;
import com.bombombom.devs.algo.model.vo.AlgorithmTaskUpdateStatus;
import com.bombombom.devs.algo.repository.AlgorithmProblemFeedbackRepository;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.algo.repository.AlgorithmProblemSolvedHistoryRedisRepository;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ForbiddenException;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.external.algo.service.AlgorithmProblemService;
import com.bombombom.devs.external.algo.service.AlgorithmProblemSolvedHistoryService;
import com.bombombom.devs.external.algo.service.dto.command.FeedbackAlgorithmProblemCommand;
import com.bombombom.devs.external.study.service.dto.command.CheckAlgorithmProblemSolvedCommand;
import com.bombombom.devs.external.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.external.study.service.dto.result.progress.AlgorithmStudyProgress;
import com.bombombom.devs.job.AlgorithmProblemConverter;
import com.bombombom.devs.solvedac.SolvedacClient;
import com.bombombom.devs.solvedac.dto.ProblemListResponse;
import com.bombombom.devs.study.enums.StudyType;
import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import com.bombombom.devs.study.model.AlgorithmProblemSolvedHistory;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.AlgorithmProblemSolvedHistoryRepository;
import com.bombombom.devs.study.repository.AlgorithmStudyDifficultyRepository;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlgorithmStudyService implements StudyProgressService {

    private final Clock clock;
    private final SolvedacClient solvedacClient;
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final RoundRepository roundRepository;
    private final UserStudyRepository userStudyRepository;
    private final AlgorithmProblemRepository algoProblemRepository;
    private final AlgorithmProblemAssignmentRepository algoAssignmentRepository;
    private final AlgorithmProblemSolvedHistoryRepository algoSolvedHistoryRepository;
    private final AlgorithmProblemFeedbackRepository algoFeedbackRepository;
    private final AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;
    private final AlgorithmProblemSolvedHistoryService algorithmProblemSolvedHistoryService;
    private final AlgorithmProblemSolvedHistoryRepository algorithmProblemSolvedHistoryRepository;
    private final AlgorithmStudyDifficultyRepository algorithmStudyDifficultyRepository;
    private final AlgorithmProblemService algorithmProblemService;
    private final AlgorithmProblemConverter algorithmProblemConverter;
    private final AlgorithmProblemSolvedHistoryRedisRepository algorithmProblemSolvedHistoryRedisRepository;

    @Override
    public StudyType getStudyType() {
        return StudyType.ALGORITHM;
    }

    @Transactional
    public AlgorithmStudyResult createStudy(
        Long userId,
        RegisterAlgorithmStudyCommand registerAlgorithmStudyCommand) {

        int difficultyGap = registerAlgorithmStudyCommand.difficultyEnd()
            - registerAlgorithmStudyCommand.difficultyBegin();
        float db = registerAlgorithmStudyCommand.difficultyBegin();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        AlgorithmStudy algorithmStudy = AlgorithmStudy.builder()
            .name(registerAlgorithmStudyCommand.name())
            .introduce(registerAlgorithmStudyCommand.introduce())
            .capacity(registerAlgorithmStudyCommand.capacity())
            .weeks(registerAlgorithmStudyCommand.weeks())
            .startDate(registerAlgorithmStudyCommand.startDate())
            .reliabilityLimit(registerAlgorithmStudyCommand.reliabilityLimit())
            .penalty(registerAlgorithmStudyCommand.penalty())
            .headCount(registerAlgorithmStudyCommand.headCount())
            .state(registerAlgorithmStudyCommand.state())
            .leader(user)
            .difficultyGap(difficultyGap)
            .problemCount(registerAlgorithmStudyCommand.problemCount())
            .build();

        algorithmStudy.createRounds();
        algorithmStudy.setDifficulty(db);

        algorithmStudy.admit(user);
        studyRepository.save(algorithmStudy);

        if (algorithmStudy.getStartDate().equals(clock.today())) {
            algorithmStudy.start(clock, userId);

            startRound(algorithmStudy, algorithmStudy.getFirstRound());
        }

        user.payMoney(algorithmStudy.calculateDeposit());
        return AlgorithmStudyResult.fromEntity(algorithmStudy);
    }

    @Override
    public AlgorithmStudyProgress findStudyProgress(Round round, List<User> members) {
        List<Long> memberIds = members.stream().map(User::getId).toList();
        List<AlgorithmProblem> problems = algorithmProblemAssignmentRepository.findAssignmentWithProblemByRoundId(
            round.getId()).stream().map(AlgorithmProblemAssignment::getProblem).toList();
        List<Long> problemIds = problems.stream().map(AlgorithmProblem::getId).toList();
        List<AlgorithmProblemSolvedHistory> histories = algorithmProblemSolvedHistoryRepository
            .findSolvedHistoryWithUserAndProblem(memberIds, problemIds);
        Map<Long, AlgorithmTaskUpdateStatus> taskUpdateStatuses = algorithmProblemSolvedHistoryRedisRepository.getTaskUpdateStatuses(
            round.getStudy().getId(), memberIds);
        return AlgorithmStudyProgress.fromEntity(round, problems, histories, taskUpdateStatuses);
    }

    @Override
    @Transactional
    public void startRound(Study study, Round round) {

        AlgorithmStudy algorithmStudy = (AlgorithmStudy) study;
        Map<AlgoTag, Integer> problemCountForEachTag =
            algorithmProblemService.getProblemCountForEachTag(algorithmStudy.getProblemCount());

        ProblemListResponse problemListResponse = solvedacClient.getUnSolvedProblems(
            study.getBaekjoonIds(), problemCountForEachTag,
            algorithmStudy.getDifficultySpreadMap());

        List<AlgorithmProblem> problems = algorithmProblemConverter.convert(problemListResponse);

        List<AlgorithmProblem> foundOrSavedProblems =
            algorithmProblemService.findProblemsThenSaveWhenNotExist(
                problems);

        assignProblemToRound(round, foundOrSavedProblems);
    }

    @Transactional
    public void updateAlgorithmTaskStatus(CheckAlgorithmProblemSolvedCommand command) {
        User user = userRepository.findById(command.userId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        if (command.problemIds().isEmpty()) {
            throw new NotFoundException(ErrorCode.PROBLEM_NOT_FOUND);
        }
        if (user.getBaekjoon() == null || user.getBaekjoon().isBlank()) {
            throw new NotFoundException(ErrorCode.BAEKJOON_ID_NOT_FOUND);
        }
        List<AlgorithmProblem> problems = algoProblemRepository.findAllById(command.problemIds());
        algorithmProblemSolvedHistoryService.addUpdateTaskStatusRequest(user, problems,
            command.studyId());
    }

    @Transactional
    public void feedback(Long userId,
        FeedbackAlgorithmProblemCommand feedbackAlgorithmProblemCommand) {

        Study study = studyRepository.findById(
                feedbackAlgorithmProblemCommand.studyId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

        if (study.getStudyType() != StudyType.ALGORITHM) {
            throw new BusinessRuleException(ErrorCode.WRONG_STUDY_TYPE);
        }

        AlgorithmStudy algorithmStudy = (AlgorithmStudy) study;

        Round round = roundRepository.findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(
                algorithmStudy.getId(), clock.today())
            .orElseThrow(() -> new NotFoundException(ErrorCode.ROUND_NOT_FOUND));

        AlgorithmProblem problem = algoProblemRepository.findById(
                feedbackAlgorithmProblemCommand.problemId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.PROBLEM_NOT_FOUND));

        AlgorithmProblemSolvedHistory history = algoSolvedHistoryRepository.findByUserIdAndProblemId(
                userId, problem.getId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.SOLVE_HISTORY_NOT_FOUND));

        if (history.getSolvedAt() == null) {
            throw new BusinessRuleException(ErrorCode.PROBLEM_NOT_SOLVED);
        }

        if (!algoAssignmentRepository.existsByRoundIdAndProblemId(round.getId(),
            problem.getId())) {
            throw new NotFoundException(ErrorCode.ASSIGNMENT_NOT_FOUND);

        }

        if (!userStudyRepository.existsByUserIdAndStudyId(userId,
            algorithmStudy.getId())) {
            throw new ForbiddenException(ErrorCode.ONLY_MEMBER_ALLOWED);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        AlgorithmProblemFeedback preFeedback =
            algoFeedbackRepository.findByUserIdAndProblemId(userId, problem.getId())
                .orElse(null);

        AlgorithmProblemFeedback newFeedback = AlgorithmProblemFeedback.builder()
            .problem(problem)
            .user(user)
            .again(feedbackAlgorithmProblemCommand.again())
            .difficulty(feedbackAlgorithmProblemCommand.difficulty())
            .build();

        if (preFeedback != null) {
            changeFeedback(algorithmStudy, preFeedback, newFeedback);
            preFeedback.update(newFeedback);

        } else {
            applyFeedback(algorithmStudy, newFeedback);
            problem.addFeedback(newFeedback);
            algoProblemRepository.save(problem);
        }
    }

    private void applyFeedback(AlgorithmStudy study, AlgorithmProblemFeedback feedback) {
        adjustDifficulty(study.getId(), feedback.getProblem().getTag(),
            study.getDifficultyVariance(feedback));
    }

    private void changeFeedback(AlgorithmStudy study, AlgorithmProblemFeedback preFeedback,
        AlgorithmProblemFeedback newFeedback) {
        adjustDifficulty(study.getId(), preFeedback.getProblem().getTag(),
            study.getDifficultyVariance(newFeedback) - study.getDifficultyVariance(preFeedback));
    }

    private void adjustDifficulty(Long studyId, AlgoTag tag, Float variance) {
        algorithmStudyDifficultyRepository.increaseDifficultyByStudyIdAndAlgoTag(studyId, tag,
            variance);
    }

    @Transactional
    public void assignProblemToRound(Round round, List<AlgorithmProblem> problems) {
        List<AlgorithmProblemAssignment> assignments = new ArrayList<>();
        for (AlgorithmProblem problem : problems) {
            assignments.add(AlgorithmProblemAssignment.of(round, problem));
        }
        algorithmProblemAssignmentRepository.saveAll(assignments);
    }
}
