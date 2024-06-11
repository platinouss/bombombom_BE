package com.bombombom.devs.study.controller;

import com.bombombom.devs.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/studies/algo")
    public String create(@ModelAttribute RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest){
        System.out.println("registerCsBookStudyRequest = " + registerAlgorithmStudyRequest);
        studyService.saveAlgo(registerAlgorithmStudyRequest.toServiceDto());
        return "index";
    }

    @PostMapping("/studies/book")
    public String create(@ModelAttribute RegisterBookStudyRequest registerBookStudyRequest){
        System.out.println("registerCsBookStudyRequest = " + registerBookStudyRequest);
        studyService.saveBook(registerBookStudyRequest.toServiceDto());
        return "index";
    }

}
