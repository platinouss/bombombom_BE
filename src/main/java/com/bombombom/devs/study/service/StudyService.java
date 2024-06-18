package com.bombombom.devs.study.service;


import com.bombombom.devs.study.controller.dto.response.AlgorithmStudyResponse;
import com.bombombom.devs.study.controller.dto.response.BookStudyResponse;
import com.bombombom.devs.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.models.UserStudy;
import com.bombombom.devs.study.repository.AlgorithmStudyRepository;
import com.bombombom.devs.study.repository.BookStudyRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.study.service.dto.command.JoinStudyCommand;
import com.bombombom.devs.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.study.service.dto.command.RegisterBookStudyCommand;
<<<<<<< HEAD
=======
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
>>>>>>> e438b8f (✨Feat: 스터디 생성 API 수정)
import com.bombombom.devs.study.service.dto.result.StudyResult;
import com.bombombom.devs.user.models.User;
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

    private final AlgorithmStudyRepository algorithmStudyRepository;
    private final BookStudyRepository bookStudyRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final UserStudyRepository userStudyRepository;


    public AlgorithmStudyResponse createAlgorithmStudy(
        RegisterAlgorithmStudyCommand registerAlgorithmStudyCommand) {
        AlgorithmStudy algorithmStudy = algorithmStudyRepository.save(
            registerAlgorithmStudyCommand.toEntity());

        return AlgorithmStudyResponse.of(AlgorithmStudyResult.fromEntity(algorithmStudy));
    }


    public BookStudyResponse createBookStudy(RegisterBookStudyCommand registerBookStudyCommand) {

        BookStudy bookStudy = bookStudyRepository.save(registerBookStudyCommand.toEntity());

        return BookStudyResponse.of(BookStudyResult.fromEntity(bookStudy));
    }

    @Transactional(readOnly = true)
    public StudyPageResponse readStudy(Pageable pageable) {
        Page<Study> studyPage = studyRepository.findAll(pageable);

        List<StudyResponse> studies = studyPage.getContent().stream()
            .map(StudyResult::fromEntity)
            .map(StudyResponse::of)
            .toList();

        return StudyPageResponse.builder()
            .contents(studies)
            .pageNumber(studyPage.getNumber())
            .totalPages(studyPage.getTotalPages())
            .totalElements(studyPage.getTotalElements())
            .build();

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
