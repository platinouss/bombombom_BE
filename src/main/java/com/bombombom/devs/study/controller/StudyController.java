package com.bombombom.devs.study.controller;

import com.bombombom.devs.global.web.LoginUser;
import com.bombombom.devs.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.study.controller.dto.response.AlgorithmStudyResponse;
import com.bombombom.devs.study.controller.dto.response.BookStudyResponse;
import com.bombombom.devs.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.service.StudyService;
import com.bombombom.devs.global.security.AppUserDetails;
import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import jakarta.validation.Valid;
import java.net.URI;
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

    public ResponseEntity<AlgorithmStudyResponse> registerAlgorithmStudy(
        @Valid @RequestBody RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest) {

        AlgorithmStudyResult algorithmStudyResult = studyService.createAlgorithmStudy(
            registerAlgorithmStudyRequest.toServiceDto());

        AlgorithmStudyResponse algorithmStudyResponse = AlgorithmStudyResponse.fromResult(
            algorithmStudyResult);

        return ResponseEntity.created(URI.create(RESOURCE_PATH + "/" + algorithmStudyResponse.id()))
            .body(algorithmStudyResponse);
    }

    @PostMapping("/book")
    public ResponseEntity<StudyResponse> registerBookStudy(
        @Valid @RequestBody RegisterBookStudyRequest registerBookStudyRequest) {

        BookStudyResult bookStudyResult = studyService.createBookStudy(
            registerBookStudyRequest.toServiceDto());

        BookStudyResponse bookStudyResponse = BookStudyResponse.fromResult(bookStudyResult);

        return ResponseEntity.created(URI.create(RESOURCE_PATH + "/" + bookStudyResponse.id()))
            .body(bookStudyResponse);
    }

    @GetMapping
    public ResponseEntity<StudyPageResponse> studyList(
        @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<StudyResponse> studyPage = studyService.readStudy(pageable)
            .map(StudyResponse::fromResult);

        StudyPageResponse studyPageResponse = StudyPageResponse.builder()
            .contents(studyPage.getContent())
            .pageNumber(studyPage.getNumber())
            .totalPages(studyPage.getTotalPages())
            .totalElements(studyPage.getTotalElements())
            .build();

        return ResponseEntity.ok(studyPageResponse);
    }

    @PostMapping("/join")
    public ResponseEntity<Void> joinStudy(@LoginUser AppUserDetails userDetails,
        @Valid @RequestBody JoinStudyRequest joinStudyRequest) {
        studyService.joinStudy(userDetails.getId(), joinStudyRequest.toServiceDto());
        return ResponseEntity.ok().build();
    }

}
