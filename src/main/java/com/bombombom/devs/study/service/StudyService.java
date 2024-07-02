package com.bombombom.devs.study.service;

import com.bombombom.devs.algo.models.AlgorithmProblem;
import com.bombombom.devs.algo.models.AlgorithmProblemConverter;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.client.solvedac.SolvedacClient;
import com.bombombom.devs.client.solvedac.dto.ProblemListResponse;
import com.bombombom.devs.global.util.Clock;
import com.bombombom.devs.study.models.AlgorithmProblemAssignment;
import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.Round;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.models.UserStudy;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.study.service.dto.command.JoinStudyCommand;
import com.bombombom.devs.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.study.service.dto.command.RegisterBookStudyCommand;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import com.bombombom.devs.user.models.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final Clock clock;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final UserStudyRepository userStudyRepository;
    private final SolvedacClient solvedacClient;
    private final RoundRepository roundRepository;
    private final AlgorithmProblemRepository algoProblemRepository;
    private final AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;
    private final AlgorithmProblemConverter algorithmProblemConverter;

    @Transactional
    public AlgorithmStudyResult createAlgorithmStudy(
        Long userId,
        RegisterAlgorithmStudyCommand registerAlgorithmStudyCommand) {

        int difficultyGap = registerAlgorithmStudyCommand.difficultyEnd()
            - registerAlgorithmStudyCommand.difficultyBegin();
        float db = registerAlgorithmStudyCommand.difficultyBegin();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User Not Found"));

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
            .difficultyGraph(db)
            .difficultyString(db)
            .difficultyImpl(db)
            .difficultyMath(db)
            .difficultyDp(db)
            .difficultyGraph(db)
            .difficultyDs(db)
            .difficultyGeometry(db)
            .difficultyGreedy(db)
            .difficultyGap(difficultyGap)
            .problemCount(registerAlgorithmStudyCommand.problemCount())
            .userStudies(new ArrayList<>())
            .rounds(new ArrayList<>())
            .build();
        algorithmStudy.createRounds();
        algorithmStudy.join(user);
        studyRepository.save(algorithmStudy);

        return AlgorithmStudyResult.fromEntity(algorithmStudy);
    }

    @Transactional
    public BookStudyResult createBookStudy(
        Long userId, RegisterBookStudyCommand registerBookStudyCommand) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User Not Found"));

        BookStudy bookStudy = BookStudy.builder()
            .name(registerBookStudyCommand.name())
            .introduce(registerBookStudyCommand.introduce())
            .capacity(registerBookStudyCommand.capacity())
            .weeks(registerBookStudyCommand.weeks())
            .startDate(registerBookStudyCommand.startDate())
            .reliabilityLimit(registerBookStudyCommand.reliabilityLimit())
            .penalty(registerBookStudyCommand.penalty())
            .headCount(registerBookStudyCommand.headCount())
            .state(registerBookStudyCommand.state())
            .leader(user)
            .bookId(registerBookStudyCommand.bookId())
            .userStudies(new ArrayList<>())
            .rounds(new ArrayList<>())
            .build();
        bookStudy.createRounds();
        bookStudy.join(user);
        studyRepository.save(bookStudy);

        return BookStudyResult.fromEntity(bookStudy);
    }

    @Transactional(readOnly = true)
    public Page<StudyResult> readStudy(Pageable pageable) {
        Page<Study> studyPage = studyRepository.findAll(pageable);

        return studyPage.map(StudyResult::fromEntity);

    }

    @Transactional
    public void joinStudy(Long userId, JoinStudyCommand joinStudyCommand) {
        if (userStudyRepository.existsByUserIdAndStudyId(userId, joinStudyCommand.studyId())) {
            throw new IllegalStateException("Already Joined Study");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User Not Found"));
        Study study = studyRepository.findById(joinStudyCommand.studyId())
            .orElseThrow(
                () -> new IllegalStateException("Study Not Found"));
        UserStudy userStudy = study.join(user);
        userStudyRepository.save(userStudy);
    }

    @Transactional
    public List<AlgorithmProblem> getUnSolvedProblemListAndSave(
        AlgorithmStudy study,
        Map<String, Integer> problemCountForEachTag
    ) {
        ProblemListResponse problemListResponse = solvedacClient.getUnSolvedProblems(
            study.getBaekjoonIds(), problemCountForEachTag, study.getDifficultySpreadForEachTag());
        List<AlgorithmProblem> problems = algorithmProblemConverter.convert(problemListResponse);
        return algoProblemRepository.saveAll(problems);
    }

    @Transactional
    public void assignProblemToRound(
        Round round, List<AlgorithmProblem> unSolvedProblems) {
        List<AlgorithmProblemAssignment> assignments = round.assignProblems(unSolvedProblems);
        algorithmProblemAssignmentRepository.saveAll(assignments);
    }

    @Transactional
    public List<Round> findRoundsHaveToStart() {
        return roundRepository.findRoundsWithStudyByStartDate(clock.today());
    }

}
