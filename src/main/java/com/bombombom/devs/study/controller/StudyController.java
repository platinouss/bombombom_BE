package com.bombombom.devs.study.controller;

import com.bombombom.devs.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.study.controller.dto.request.SearchStudyRequest;
import com.bombombom.devs.study.controller.dto.response.SearchStudyResponse;
import com.bombombom.devs.study.service.StudyService;
import com.bombombom.devs.study.service.dto.command.RegisterAlgorithmStudyCommand;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StudyController {

    private static final String STUDY_DOMAIN = "/api/v1/studies/";
    private final StudyService studyService;

    @PostMapping("/studies/algo")
    public ResponseEntity<Void> registerAlgorithmStudy(@RequestBody RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest){
        log.debug("{}", registerAlgorithmStudyRequest);
        Long id = studyService.saveAlgo(registerAlgorithmStudyRequest.toServiceDto());
        return ResponseEntity.created(URI.create(STUDY_DOMAIN+id)).build();
    }

    @PostMapping("/studies/book")
    public ResponseEntity<Void> registerBookStudy(@RequestBody RegisterBookStudyRequest registerBookStudyRequest){
        log.debug("{}", registerBookStudyRequest);
        Long id =studyService.saveBook(registerBookStudyRequest.toServiceDto());
        return ResponseEntity.created(URI.create(STUDY_DOMAIN+id)).build();
    }

    @GetMapping("/studies")
    public ResponseEntity<List<SearchStudyResponse>> searchStudy(@RequestParam SearchStudyRequest searchStudyRequest){
//        studyService.saveBook(searchStudyRequest.toServiceDto());
        List<SearchStudyResponse> responses = null;
        return null;
    }

}
