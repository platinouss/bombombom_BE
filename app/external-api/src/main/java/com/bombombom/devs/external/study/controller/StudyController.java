package com.bombombom.devs.external.study.controller;

import com.bombombom.devs.external.algo.controller.dto.request.FeedbackAlgorithmProblemRequest;
import com.bombombom.devs.external.global.security.AppUserDetails;
import com.bombombom.devs.external.global.web.LoginUser;
import com.bombombom.devs.external.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.StartStudyRequest;
import com.bombombom.devs.external.study.controller.dto.response.AlgorithmStudyResponse;
import com.bombombom.devs.external.study.controller.dto.response.BookStudyResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyDetailsResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyProgressResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.external.study.service.AlgorithmStudyService;
import com.bombombom.devs.external.study.service.StudyService;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.external.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(StudyController.RESOURCE_PATH)
public class StudyController {

    public static final String RESOURCE_PATH = "/api/v1/studies";
    private final StudyService studyService;
    private final AlgorithmStudyService algorithmStudyService;


    @PostMapping("/algo")
    public ResponseEntity<AlgorithmStudyResponse> registerAlgorithmStudy(
        @LoginUser AppUserDetails userDetails,
        @Valid @RequestBody RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest) {

        AlgorithmStudyResult algorithmStudyResult = studyService.createAlgorithmStudy(
            userDetails.getId(),
            registerAlgorithmStudyRequest.toServiceDto());

        AlgorithmStudyResponse algorithmStudyResponse = AlgorithmStudyResponse.fromResult(
            algorithmStudyResult);

        return ResponseEntity.created(URI.create(RESOURCE_PATH + "/" + algorithmStudyResponse.id()))
            .body(algorithmStudyResponse);
    }

    @PostMapping("/book")
    public ResponseEntity<StudyResponse> registerBookStudy(
        @LoginUser AppUserDetails userDetails,
        @Valid @RequestBody RegisterBookStudyRequest registerBookStudyRequest) {

        BookStudyResult bookStudyResult = studyService.createBookStudy(
            userDetails.getId(),
            registerBookStudyRequest.toServiceDto());

        BookStudyResponse bookStudyResponse = BookStudyResponse.fromResult(bookStudyResult);

        return ResponseEntity.created(URI.create(RESOURCE_PATH + "/" + bookStudyResponse.id()))
            .body(bookStudyResponse);
    }

    @GetMapping
    public ResponseEntity<StudyPageResponse> studyList(
        @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable) {

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
    public ResponseEntity<Void> joinStudy(
        @LoginUser AppUserDetails userDetails,
        @Valid @RequestBody JoinStudyRequest joinStudyRequest
    ) {
        studyService.joinStudy(userDetails.getId(), joinStudyRequest.toServiceDto());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyDetailsResponse> getStudyDetails(@PathVariable("id") Long studyId) {
        StudyDetailsResult studyDetailsResult = studyService.findStudyDetails(studyId);
        return ResponseEntity.ok().body(StudyDetailsResponse.fromResult(studyDetailsResult));
    }

    @GetMapping("/progress/{id}")
    public ResponseEntity<StudyProgressResponse> progressStudy(@PathVariable("id") Long studyId,
        @RequestParam Integer idx) {
        StudyProgressResult studyProgressResult = studyService.findStudyProgress(studyId, idx);
        return ResponseEntity.ok().body(StudyProgressResponse.fromResult(studyProgressResult));
    }


    @PostMapping("/feedback")
    public ResponseEntity<Void> feedback(
        @LoginUser AppUserDetails userDetails,
        @Valid @RequestBody FeedbackAlgorithmProblemRequest feedbackAlgorithmProblemRequest) {

        algorithmStudyService.feedback(userDetails.getId(),
            feedbackAlgorithmProblemRequest.toServiceDto());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/start")
    public ResponseEntity<Void> start(
        @LoginUser AppUserDetails userDetails,

        @Valid @RequestBody StartStudyRequest startStudyRequest) {

        studyService.start(userDetails.getId(),
            startStudyRequest.toServiceDto());

        return ResponseEntity.ok().build();
    }

}
