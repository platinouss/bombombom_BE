package com.bombombom.devs.external.study.service;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.external.study.exception.NotFoundException;
import com.bombombom.devs.external.study.service.dto.command.JoinStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.RegisterBookStudyCommand;
import com.bombombom.devs.external.study.service.dto.query.GetAlgorithmStudyProgressQuery;
import com.bombombom.devs.external.study.service.dto.query.GetStudyProgressQuery;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.external.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.external.study.service.dto.result.StudyAndRoundResult;
import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.StudyResult;
import com.bombombom.devs.external.study.service.dto.result.progress.AlgorithmStudyProgress;
import com.bombombom.devs.global.util.Clock;
import com.bombombom.devs.job.AlgorithmProblemConverter;
import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import com.bombombom.devs.study.model.AlgorithmProblemAssignmentSolveHistory;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.BookStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.study.model.UserStudy;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentSolveHistoryRepository;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
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
    private final BookRepository bookRepository;
    private final UserStudyRepository userStudyRepository;
    private final RoundRepository roundRepository;
    private final AlgorithmProblemRepository algoProblemRepository;
    private final AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;
    private final AlgorithmProblemConverter algorithmProblemConverter;
    private final AlgorithmProblemAssignmentSolveHistoryRepository algorithmProblemSolveHistoryRepository;

    @Transactional
    public AlgorithmStudyResult createAlgorithmStudy(
        Long userId,
        RegisterAlgorithmStudyCommand registerAlgorithmStudyCommand) {

        int difficultyGap = registerAlgorithmStudyCommand.difficultyEnd()
            - registerAlgorithmStudyCommand.difficultyBegin();
        float db = registerAlgorithmStudyCommand.difficultyBegin();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User Not Found"));

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

        algorithmStudy.check(user);
        algorithmStudy.admit(user);
        studyRepository.save(algorithmStudy);

        user.payMoney(algorithmStudy.calculateDeposit());
        return AlgorithmStudyResult.fromEntity(algorithmStudy);
    }

    @Transactional
    public BookStudyResult createBookStudy(
        Long userId, RegisterBookStudyCommand registerBookStudyCommand) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User Not Found"));

        Book book = bookRepository.findByIsbn(registerBookStudyCommand.isbn())
            .orElseThrow(() -> new NotFoundException("Book Not Found"));

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
            .userStudies(new ArrayList<>())
            .rounds(new ArrayList<>())
            .book(book)
            .build();

        bookStudy.createRounds();

        bookStudy.check(user);
        bookStudy.admit(user);
        studyRepository.save(bookStudy);

        user.payMoney(bookStudy.calculateDeposit());
        return BookStudyResult.fromEntity(bookStudy);
    }

    @Transactional(readOnly = true)
    public Page<StudyResult> readStudy(Pageable pageable) {
        Page<Study> studyPage = studyRepository.findAllWithUserAndBook(pageable);

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

        study.check(user);
        study.admit(user);

        user.payMoney(study.calculateDeposit());
    }

    @Transactional
    public void assignProblemToRound(Round round, List<AlgorithmProblem> problems) {
        round.assignProblems(problems);
        roundRepository.save(round);
    }

    @Transactional
    public List<Round> findRoundsHaveToStart() {
        return roundRepository.findRoundsWithStudyByStartDate(clock.today());
    }

    @Transactional(readOnly = true)
    public StudyAndRoundResult findStudyAndRound(Long studyId, Integer roundIdx) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new IllegalStateException("Study Not Found"));
        Round round = roundRepository.findRoundByStudyAndIdx(studyId, roundIdx)
            .orElseThrow(() -> new IllegalStateException("Round Not Found"));
        return StudyAndRoundResult.fromEntity(study, round);
    }

    @Transactional(readOnly = true)
    public StudyDetailsResult<?> findStudyDetails(Long studyId) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new IllegalStateException("Study Not Found"));
        Round currentRound = roundRepository.findRoundByStudyIdAndEndDate(studyId,
                study.getWeeks() - 1, clock.today())
            .orElseThrow(() -> new IllegalStateException("Round Not Found"));
        return StudyDetailsResult.fromResult(study,
            findStudyProgress(GetStudyProgressQuery.fromEntity(study, currentRound)));
    }

    @Transactional(readOnly = true)
    public StudyProgressResult<?> findStudyProgress(GetStudyProgressQuery query) {
        List<User> studyMembers = userStudyRepository.findByStudyId(query.study().getId())
            .stream().map(UserStudy::getUser).toList();
        if (query.study().getStudyType() == StudyType.ALGORITHM) {
            return AlgorithmStudyProgressResult.fromEntity(StudyType.ALGORITHM, studyMembers,
                findAlgorithmStudyProgress(query.toAlgorithmProgressQuery(studyMembers)));
        }
        return null;
    }

    AlgorithmStudyProgress findAlgorithmStudyProgress(GetAlgorithmStudyProgressQuery query) {
        List<AlgorithmProblem> problems = algorithmProblemAssignmentRepository.findProblemWithStudyByRound(
            query.round().getId()).stream().map(AlgorithmProblemAssignment::getProblem).toList();
        List<Long> problemsId = problems.stream().map(AlgorithmProblem::getId).toList();
        List<AlgorithmProblemAssignmentSolveHistory> histories = algorithmProblemSolveHistoryRepository
            .findSolvedHistoryWithUserAndProblem(query.studyMembersId(), problemsId);
        return AlgorithmStudyProgress.fromEntity(query.round(), problems, histories);
    }
}
