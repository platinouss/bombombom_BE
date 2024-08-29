package com.bombombom.devs.external.study.service;

import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.external.study.service.dto.command.JoinStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.StartStudyCommand;
import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.StudyResult;
import com.bombombom.devs.external.study.service.factory.StudyServiceFactory;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.UserStudy;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
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
    private final UserStudyRepository userStudyRepository;
    private final RoundRepository roundRepository;
    private final StudyServiceFactory studyServiceFactory;


    @Transactional(readOnly = true)
    public Page<StudyResult> readStudy(Pageable pageable) {
        Page<Study> studyPage = studyRepository.findAllWithDifficultiesAndLeaderAndBook(pageable);

        return studyPage.map(StudyResult::fromEntity);

    }

    @Transactional
    public void joinStudy(Long userId, JoinStudyCommand joinStudyCommand) {
        if (userStudyRepository.existsByUserIdAndStudyId(userId, joinStudyCommand.studyId())) {
            throw new IllegalStateException("Already Joined Study");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User Not Found"));

        Study study = studyRepository.findByIdForUpdate(joinStudyCommand.studyId())
            .orElseThrow(
                () -> new IllegalStateException("Study Not Found"));

        study.admit(user);

        // 유저에 락킹??
        user.payMoney(study.calculateDeposit());
    }


    @Transactional
    public List<Round> findRoundsHaveToStart() {
        return roundRepository.findRoundsWithStudyByStartDate(clock.today());
    }

    @Transactional(readOnly = true)
    public StudyDetailsResult findStudyDetails(Long studyId) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new IllegalStateException("Study Not Found"));

        Round currentRound = roundRepository.findRoundByStudyIdAndBetweenStartDateAndEndDateOrIdx(
                studyId, study.getWeeks() - 1, clock.today())
            .orElseThrow(() -> new IllegalStateException("Round Not Found"));
        return StudyDetailsResult.fromResult(study, findStudyProgress(study, currentRound));
    }

    @Transactional(readOnly = true)
    public StudyProgressResult findStudyProgress(Long studyId, Integer roundIdx) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new IllegalStateException("Study Not Found"));
        Round round = roundRepository.findRoundByStudyAndIdx(studyId, roundIdx)
            .orElseThrow(() -> new IllegalStateException("Round Not Found"));
        return findStudyProgress(study, round);
    }

    @Transactional(readOnly = true)
    public StudyProgressResult findStudyProgress(Study study, Round round) {
        List<User> members = userStudyRepository.findByStudyId(study.getId()).stream()
            .map(UserStudy::getUser).toList();
        return StudyProgressResult.fromEntity(study.getStudyType(), members,
            studyServiceFactory.getService(study.getStudyType()).findStudyProgress(round, members));
    }


    @Transactional
    public void start(Long userId, StartStudyCommand startStudyCommand) {

        Study study = studyRepository.findWithLeaderById(
                startStudyCommand.studyId())
            .orElseThrow(() -> new NotFoundException("Study Not Found"));

        study.start(clock, userId);

        studyServiceFactory.getService(study.getStudyType())
            .startRound(study, study.getFirstRound());

    }


}
