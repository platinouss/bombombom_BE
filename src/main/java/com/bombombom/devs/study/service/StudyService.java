package com.bombombom.devs.study.service;

import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.models.UserStudy;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final UserStudyRepository userStudyRepository;


    public AlgorithmStudyResult createAlgorithmStudy(
        RegisterAlgorithmStudyCommand registerAlgorithmStudyCommand) {

        int difficultyGap = registerAlgorithmStudyCommand.difficultyEnd()
            - registerAlgorithmStudyCommand.difficultyBegin();
        float db = registerAlgorithmStudyCommand.difficultyBegin();

        AlgorithmStudy algorithmStudy = AlgorithmStudy.builder()
            .name(registerAlgorithmStudyCommand.name())
            .introduce(registerAlgorithmStudyCommand.introduce())
            .capacity(registerAlgorithmStudyCommand.capacity())
            .weeks(registerAlgorithmStudyCommand.weeks())
            .startDate(registerAlgorithmStudyCommand.startDate())
            .reliabilityLimit(registerAlgorithmStudyCommand.reliabilityLimit())
            .penalty(registerAlgorithmStudyCommand.penalty())
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
            .build();

        return AlgorithmStudyResult.fromEntity(studyRepository.save(
            algorithmStudy));
    }


    public BookStudyResult createBookStudy(RegisterBookStudyCommand registerBookStudyCommand) {

        BookStudy bookStudy = BookStudy.builder()
            .name(registerBookStudyCommand.name())
            .introduce(registerBookStudyCommand.introduce())
            .capacity(registerBookStudyCommand.capacity())
            .weeks(registerBookStudyCommand.weeks())
            .startDate(registerBookStudyCommand.startDate())
            .reliabilityLimit(registerBookStudyCommand.reliabilityLimit())
            .penalty(registerBookStudyCommand.penalty())
            .bookId(registerBookStudyCommand.bookId())
            .build();

        return BookStudyResult.fromEntity(studyRepository.save(bookStudy));
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
}
