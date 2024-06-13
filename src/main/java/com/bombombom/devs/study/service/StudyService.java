package com.bombombom.devs.study.service;

import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.repository.AlgorithmStudyRepository;
import com.bombombom.devs.study.repository.BookStudyRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.study.service.dto.command.RegisterBookStudyCommand;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<StudyResponse> readStudy(Pageable pageable) {
        Page<Study> studies = studyRepository.findAll(pageable);

        return studies.getContent().stream().map(StudyResult::fromEntity).map(StudyResponse::of)
            .toList();
    }

}
