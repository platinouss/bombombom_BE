package com.bombombom.devs.study.service;


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
import com.bombombom.devs.study.service.dto.result.StudyResult;
import com.bombombom.devs.user.models.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final AlgorithmStudyRepository algorithmStudyRepository;
    private final BookStudyRepository bookStudyRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final UserStudyRepository userStudyRepository;


    public Long createAlgorithmStudy(RegisterAlgorithmStudyCommand registerAlgorithmStudyCommand) {
        AlgorithmStudy algorithmStudy = algorithmStudyRepository.save(
            registerAlgorithmStudyCommand.toEntity());

        return algorithmStudy.getId();
    }

    public Long createBookStudy(RegisterBookStudyCommand registerBookStudyCommand) {
        BookStudy bookStudy = bookStudyRepository.save(registerBookStudyCommand.toEntity());

        return bookStudy.getId();
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
    public void joinAlgorithmStudy(Long userId, JoinStudyCommand joinStudyCommand) {
        if (userStudyRepository.existsByUserIdAndStudyId(userId, joinStudyCommand.studyId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already Joined Study");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
        Study study = studyRepository.findById(joinStudyCommand.studyId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study Not Found"));
        UserStudy userStudy = study.join(user);
        userStudyRepository.save(userStudy);
    }
}
