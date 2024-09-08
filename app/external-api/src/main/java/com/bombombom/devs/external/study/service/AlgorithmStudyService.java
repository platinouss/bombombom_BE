package com.bombombom.devs.external.study.service;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.algo.model.AlgorithmProblemFeedback;
import com.bombombom.devs.algo.repository.AlgorithmProblemFeedbackRepository;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.exception.NotAcceptableException;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.external.algo.service.AlgorithmProblemService;
import com.bombombom.devs.external.algo.service.dto.command.FeedbackAlgorithmProblemCommand;
import com.bombombom.devs.external.study.service.dto.result.progress.AlgorithmStudyProgress;
import com.bombombom.devs.job.AlgorithmProblemConverter;
import com.bombombom.devs.solvedac.SolvedacClient;
import com.bombombom.devs.solvedac.dto.ProblemListResponse;
import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import com.bombombom.devs.study.model.AlgorithmProblemSolveHistory;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.AlgorithmProblemSolveHistoryRepository;
import com.bombombom.devs.study.repository.AlgorithmStudyDifficultyRepository;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlgorithmStudyService implements StudyProgressService {

    private final StudyRepository studyRepository;
    private final RoundRepository roundRepository;
    private final UserRepository userRepository;
    private final UserStudyRepository userStudyRepository;
    private final AlgorithmProblemRepository algoProblemRepository;
    private final AlgorithmProblemAssignmentRepository algoAssignmentRepository;
    private final AlgorithmProblemSolveHistoryRepository algoSolveHistoryRepository;
    private final AlgorithmProblemFeedbackRepository algoFeedbackRepository;
    private final AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;
    private final AlgorithmProblemSolveHistoryRepository algorithmProblemSolveHistoryRepository;
    private final AlgorithmStudyDifficultyRepository algorithmStudyDifficultyRepository;
    private final SolvedacClient solvedacClient;
    private final AlgorithmProblemService algorithmProblemService;
    private final AlgorithmProblemConverter algorithmProblemConverter;
    private final Clock clock;

    @Override
    public StudyType getStudyType() {
        return StudyType.ALGORITHM;
    }

    @Override
    public AlgorithmStudyProgress findStudyProgress(Round round, List<User> members) {
        List<Long> membersId = members.stream().map(User::getId).toList();
        List<AlgorithmProblem> problems = algorithmProblemAssignmentRepository.findAssignmentWithProblemByRoundId(
            round.getId()).stream().map(AlgorithmProblemAssignment::getProblem).toList();
        List<Long> problemsId = problems.stream().map(AlgorithmProblem::getId).toList();
        List<AlgorithmProblemSolveHistory> histories = algorithmProblemSolveHistoryRepository
            .findSolvedHistoryWithUserAndProblem(membersId, problemsId);
        return AlgorithmStudyProgress.fromEntity(round, problems, histories);
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
    public void feedback(Long userId,
        FeedbackAlgorithmProblemCommand feedbackAlgorithmProblemCommand) {

        Study study = studyRepository.findById(
                feedbackAlgorithmProblemCommand.studyId())
            .orElseThrow(() -> new NotFoundException("Study Not Found"));

        if (study.getStudyType() != StudyType.ALGORITHM) {
            throw new NotAcceptableException("Feedback can only be given to Algorithm Study");
        }

        AlgorithmStudy algorithmStudy = (AlgorithmStudy) study;

        Round round = roundRepository.findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(
                algorithmStudy.getId(), clock.today())
            .orElseThrow(() -> new NotFoundException("Ongoing Round Not Found"));

        AlgorithmProblem problem = algoProblemRepository.findById(
                feedbackAlgorithmProblemCommand.problemId())
            .orElseThrow(() -> new NotFoundException("Problem Not Found"));

        AlgorithmProblemSolveHistory history = algoSolveHistoryRepository.findByUserIdAndProblemId(
                userId, problem.getId())
            .orElseThrow(() -> new NotFoundException("Solve History Not Found"));

        if (history.getSolvedAt() == null) {
            throw new IllegalStateException("Cant Give Feedback On Unsolved Problem");
        }

        if (!algoAssignmentRepository.existsByRoundIdAndProblemId(round.getId(),
            problem.getId())) {
            throw new NotAcceptableException("Problem is not ongoing assignment");

        }

        if (!userStudyRepository.existsByUserIdAndStudyId(userId,
            algorithmStudy.getId())) {
            throw new NotAcceptableException("User is not a member");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User Not Found"));

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
        round.assignProblems(problems);
        roundRepository.save(round);
    }
}
