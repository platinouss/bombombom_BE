package com.bombombom.devs.external.study.service;

import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.external.study.service.dto.command.ConfigureStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.JoinStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.StartStudyCommand;
import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.StudyResult;
import com.bombombom.devs.external.study.service.dto.result.progress.StudyProgress;
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
import java.util.concurrent.atomic.AtomicInteger;
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
        Page<Long> studyPage = studyRepository.findIdsAll(pageable);

        List<Study> studies = studyRepository.findWithDifficultiesAndLeaderAndBookByIds(
            studyPage.getContent());
        AtomicInteger index = new AtomicInteger();
        
        return studyPage.map(i -> studies.get(index.getAndIncrement()))
            .map(StudyResult::fromEntity);

    }

    @Transactional
    public void joinStudy(Long userId, JoinStudyCommand joinStudyCommand) {
        if (userStudyRepository.existsByStudyIdAndUserId(joinStudyCommand.studyId(), userId)) {
            throw new BusinessRuleException(ErrorCode.ALREADY_JOINED);
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Study study = studyRepository.findByIdForUpdate(joinStudyCommand.studyId())
            .orElseThrow(
                () -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

        study.admit(user);

        user.payMoney(study.calculateDeposit());
    }


    @Transactional
    public List<Round> findRoundsHaveToStart() {
        return roundRepository.findRoundsWithStudyByStartDate(clock.today());
    }

    @Transactional(readOnly = true)
    public StudyDetailsResult findStudyDetails(Long studyId) {
        Study study = studyRepository.findWithDifficultiesAndLeaderAndBookById(studyId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

        Round currentRound = roundRepository.findRoundByStudyIdAndBetweenStartDateAndEndDateOrIdx(
                studyId, study.getWeeks() - 1, clock.today())
            .orElseThrow(() -> new NotFoundException(ErrorCode.ROUND_NOT_FOUND));
        return new StudyDetailsResult(StudyResult.fromEntity(study),
            findStudyProgress(study, currentRound));
    }

    @Transactional
    public void configure(Long userId,
        Long studyId, ConfigureStudyCommand command) {

        command.assertAnyNotNull();

        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

        study.assertLeader(userId);

        study.setDuplicated(command.duplicated());

    }

    @Transactional(readOnly = true)
    public StudyProgressResult findStudyProgress(Long studyId, Integer roundIdx) {
        Study study = studyRepository.findWithDifficultiesAndLeaderAndBookById(studyId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));
        Round round = roundRepository.findRoundByStudyAndIdx(studyId, roundIdx)
            .orElseThrow(() -> new NotFoundException(ErrorCode.ROUND_NOT_FOUND));
        return findStudyProgress(study, round);
    }

    @Transactional(readOnly = true)
    public StudyProgressResult findStudyProgress(Study study, Round round) {
        List<User> members = userStudyRepository.findWithUserByStudyId(study.getId()).stream()
            .map(UserStudy::getUser).toList();
        StudyProgress studyProgress = studyServiceFactory.getService(study.getStudyType())
            .findStudyProgress(round, members);
        return StudyProgressResult.fromEntity(study.getStudyType(), members, studyProgress);
    }


    @Transactional
    public void start(Long userId, StartStudyCommand startStudyCommand) {

        Study study = studyRepository.findWithLeaderById(
                startStudyCommand.studyId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

        study.start(clock, userId);

        studyServiceFactory.getService(study.getStudyType())
            .startRound(study, study.getFirstRound());

    }

    @Transactional
    public void startVoting(Long userId, Long studyId) {

        Study study = studyRepository.findWithLeaderById(
                studyId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

        study.startVoting(userId);

        studyRepository.save(study);
    }

    public List<StudyResult> getOwnedStudies(Long userId) {
        List<Study> studies = studyRepository.findAllByLeader(userId);
        return studies.stream().map(StudyResult::fromEntity).toList();
    }
}
