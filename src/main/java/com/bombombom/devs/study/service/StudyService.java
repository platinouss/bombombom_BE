package com.bombombom.devs.study.service;

import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.repository.AlgorithmStudyRepository;
import com.bombombom.devs.study.repository.BookStudyRepository;
import com.bombombom.devs.study.service.dto.command.RegisterAlgorithmStudyCommand;
import com.bombombom.devs.study.service.dto.command.RegisterBookStudyCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final AlgorithmStudyRepository algorithmStudyRepository;
    private final BookStudyRepository bookStudyRepository;


    public Long saveAlgo(RegisterAlgorithmStudyCommand registerAlgorithmStudyCommand){
        AlgorithmStudy algorithmStudy = algorithmStudyRepository.save(registerAlgorithmStudyCommand.toEntity());

        return algorithmStudy.getId();
    }

    public Long saveBook(RegisterBookStudyCommand registerBookStudyCommand){

        BookStudy bookStudy =bookStudyRepository.save( registerBookStudyCommand.toEntity());

        return bookStudy.getId();
    }


}
