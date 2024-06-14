package com.bombombom.devs.study.controller;

import com.bombombom.devs.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.service.StudyService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(StudyController.RESOURCE_PATH)
public class StudyController {

    public static final String RESOURCE_PATH = "/api/v1/studies";
    private final StudyService studyService;

    @PostMapping("/algo")
    public ResponseEntity<Void> registerAlgorithmStudy(@RequestBody RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest){
        log.info("{}", registerAlgorithmStudyRequest);
        Long id = studyService.createAlgorithmStudy(registerAlgorithmStudyRequest.toServiceDto());
        return ResponseEntity.created(URI.create(RESOURCE_PATH+"/"+id)).build();
    }

    @PostMapping("/book")
    public ResponseEntity<Void> registerBookStudy(@RequestBody RegisterBookStudyRequest registerBookStudyRequest){
        log.info("{}", registerBookStudyRequest);
        Long id =studyService.createBookStudy(registerBookStudyRequest.toServiceDto());
        return ResponseEntity.created(URI.create(RESOURCE_PATH+id)).build();
    }

    @GetMapping
    public ResponseEntity<List<StudyResponse>> studyList(@PageableDefault(sort="id", direction= Sort.Direction.DESC) Pageable pageable){
        List<StudyResponse> studyResponses = studyService.readStudy(pageable);
        log.info("{}", studyResponses);
        return ResponseEntity.ok(studyResponses);
    }

}
