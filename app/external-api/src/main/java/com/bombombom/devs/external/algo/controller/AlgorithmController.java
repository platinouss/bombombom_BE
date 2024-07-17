package com.bombombom.devs.external.algo.controller;

import com.bombombom.devs.external.algo.controller.dto.request.FeedbackAlgorithmProblemRequest;
import com.bombombom.devs.external.algo.service.AlgorithmProblemService;
import com.bombombom.devs.external.global.security.AppUserDetails;
import com.bombombom.devs.external.global.web.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/algo")
public class AlgorithmController {

    private final AlgorithmProblemService algorithmProblemService;


    @PostMapping("/feedback")
    public ResponseEntity<Void> feedback(
        @LoginUser AppUserDetails userDetails,
        @Valid @RequestBody FeedbackAlgorithmProblemRequest feedbackAlgorithmProblemRequest) {

        algorithmProblemService.feedback(userDetails.getId(),
            feedbackAlgorithmProblemRequest.toServiceDto());

        return ResponseEntity.ok().build();
    }

}
