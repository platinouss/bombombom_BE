package com.bombombom.devs.study.controller;

import com.bombombom.devs.global.web.LoginUser;
import com.bombombom.devs.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.study.service.StudyService;
import com.bombombom.devs.global.security.AppUserDetails;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<Void> registerAlgorithmStudy(
        @Valid @RequestBody RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest) {
        log.info("{}", registerAlgorithmStudyRequest);
        Long id = studyService.createAlgorithmStudy(registerAlgorithmStudyRequest.toServiceDto());
        return ResponseEntity.created(URI.create(RESOURCE_PATH + "/" + id)).build();
    }

    @PostMapping("/book")
    public ResponseEntity<Void> registerBookStudy(
        @Valid @RequestBody RegisterBookStudyRequest registerBookStudyRequest) {
        log.info("{}", registerBookStudyRequest);
        Long id = studyService.createBookStudy(registerBookStudyRequest.toServiceDto());
        return ResponseEntity.created(URI.create(RESOURCE_PATH + id)).build();
    }

    @GetMapping
    public ResponseEntity<StudyPageResponse> studyList(
        @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        StudyPageResponse studyPageResponse = studyService.readStudy(pageable);
        return ResponseEntity.ok(studyPageResponse);
    }

    @PostMapping("/join")
    public ResponseEntity<Void> joinStudy(@LoginUser AppUserDetails userDetails,
        @Valid @RequestBody JoinStudyRequest joinStudyRequest) {
        studyService.joinStudy(userDetails.getId(), joinStudyRequest.toServiceDto());
        return ResponseEntity.ok().build();
    }

}
