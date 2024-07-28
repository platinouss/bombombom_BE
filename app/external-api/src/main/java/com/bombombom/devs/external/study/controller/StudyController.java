package com.bombombom.devs.external.study.controller;

import com.bombombom.devs.external.algo.controller.dto.request.FeedbackAlgorithmProblemRequest;
import com.bombombom.devs.external.global.web.LoginUser;
import com.bombombom.devs.external.study.controller.dto.request.AddAssignmentRequest;
import com.bombombom.devs.external.study.controller.dto.request.CheckAlgorithmProblemSolvedRequest;
import com.bombombom.devs.external.study.controller.dto.request.ConfigureStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.DeleteAssignmentRequest;
import com.bombombom.devs.external.study.controller.dto.request.EditAssignmentRequest;
import com.bombombom.devs.external.study.controller.dto.request.EditAssignmentRequest.AssignmentInfo;
import com.bombombom.devs.external.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.RegisterAlgorithmStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.RegisterBookStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.StartStudyRequest;
import com.bombombom.devs.external.study.controller.dto.request.VoteAssignmentRequest;
import com.bombombom.devs.external.study.controller.dto.response.AlgorithmStudyResponse;
import com.bombombom.devs.external.study.controller.dto.response.AlgorithmStudyTaskStatusResponse;
import com.bombombom.devs.external.study.controller.dto.response.BookStudyResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyDetailsResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyProgressResponse;
import com.bombombom.devs.external.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.external.study.service.AlgorithmStudyService;
import com.bombombom.devs.external.study.service.BookStudyService;
import com.bombombom.devs.external.study.service.StudyService;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.external.study.service.dto.result.AssignmentResult;
import com.bombombom.devs.external.study.service.dto.result.AssignmentVoteResult;
import com.bombombom.devs.external.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.external.study.service.dto.result.SolvedAlgorithmProblemResult;
import com.bombombom.devs.external.study.service.dto.result.StudyDetailsResult;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.security.AppUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(StudyController.RESOURCE_PATH)
public class StudyController {

    public static final String RESOURCE_PATH = "/api/v1/studies";

    private final StudyService studyService;
    private final AlgorithmStudyService algorithmStudyService;
    private final BookStudyService bookStudyService;

    @PostMapping("/algo")
    public ResponseEntity<AlgorithmStudyResponse> registerAlgorithmStudy(
        @LoginUser AppUserDetails userDetails,
        @Valid @RequestBody RegisterAlgorithmStudyRequest registerAlgorithmStudyRequest) {

        AlgorithmStudyResult algorithmStudyResult = algorithmStudyService.createStudy(
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

        BookStudyResult bookStudyResult = bookStudyService.createStudy(
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

    @PostMapping("/algo/status")
    public ResponseEntity<AlgorithmStudyTaskStatusResponse> checkAlgorithmProblemSolved(
        @Valid @RequestBody CheckAlgorithmProblemSolvedRequest checkAlgorithmProblemSolvedRequest) {
        SolvedAlgorithmProblemResult solvedAlgorithmProblemResult = algorithmStudyService.checkAlgorithmProblemSolved(
            checkAlgorithmProblemSolvedRequest.toServiceDto());
        return ResponseEntity.ok()
            .body(AlgorithmStudyTaskStatusResponse.fromResult(solvedAlgorithmProblemResult));
    }

    @PostMapping("/{id}/start-voting")
    public ResponseEntity<Void> startVoting(
        @LoginUser AppUserDetails userDetails,
        @PathVariable("id") Long studyId) {

        studyService.startVoting(userDetails.getId(), studyId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/assignments")
    public ResponseEntity<List<AssignmentResult>> editAssignments(
        @LoginUser AppUserDetails userDetails,
        @PathVariable("id") Long studyId,
        @Valid @RequestBody EditAssignmentRequest editAssignmentRequest) {

        List<AssignmentResult> assignmentResults = bookStudyService.setAssignments(
            userDetails.getId(), studyId,
            editAssignmentRequest.toServiceDto());

        return ResponseEntity.ok().body(assignmentResults);
    }

    @PatchMapping("/{id}/config")
    public ResponseEntity<Void> configureStudy(
        @LoginUser AppUserDetails userDetails,
        @PathVariable("id") Long studyId,
        @Valid @RequestBody ConfigureStudyRequest configureStudyRequest) {

        studyService.configure(
            userDetails.getId(),
            studyId,
            configureStudyRequest.toServiceDto());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/vote")
    public ResponseEntity<AssignmentVoteResult> voteAssignment(
        @LoginUser AppUserDetails userDetails,
        @PathVariable("id") Long studyId,
        @Valid @RequestBody VoteAssignmentRequest voteAssignmentRequest) {

        AssignmentVoteResult assignmentResults = bookStudyService.voteAssignment(
            userDetails.getId(), studyId,
            voteAssignmentRequest.toServiceDto());

        return ResponseEntity.ok().body(assignmentResults);
    }

    @DeleteMapping("/{id}/assignments")
    public ResponseEntity<List<Long>> deleteAssignments(
        @LoginUser AppUserDetails userDetails,
        @PathVariable("id") Long studyId,
        @Valid @RequestBody DeleteAssignmentRequest deleteAssignmentRequest) {

        bookStudyService.removeAssignments(userDetails.getId(), studyId,
            deleteAssignmentRequest.toServiceDto());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/assignments")
    public ResponseEntity<List<AssignmentInfo>> getAssignments(
        @PathVariable("id") Long studyId,
        @RequestParam @NotNull @Min(0) Integer roundIdx) {

        List<AssignmentResult> assignments =
            bookStudyService.getAssignments(studyId, roundIdx);

        return ResponseEntity.ok().body(assignments.stream().map(
            AssignmentInfo::fromResult).toList());
    }

    @PostMapping("/{id}/assignments")
    public ResponseEntity<List<AssignmentResult>> createAssignments(
        @LoginUser AppUserDetails userDetails,
        @PathVariable("id") Long studyId,
        @Valid @RequestBody AddAssignmentRequest editAssignmentRequest) {

        List<AssignmentResult> assignmentResults = bookStudyService.addAssignments(
            userDetails.getId(),
            studyId,
            editAssignmentRequest.toServiceDto());

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/{id}").buildAndExpand(editAssignmentRequest.roundIdx()).toUri();

        return ResponseEntity.created(location).body(assignmentResults);
    }

    @GetMapping("/owned")
    public ResponseEntity<List<StudyResponse>> ownedStudies(
        @LoginUser AppUserDetails userDetails
    ) {

        return ResponseEntity.ok(studyService.getOwnedStudies(userDetails.getId()).stream().map(
            StudyResponse::fromResult
        ).toList());
    }

}
