package com.bombombom.devs.external.video.controller;

import com.bombombom.devs.S3PreSignedUrlProvider;
import com.bombombom.devs.external.video.controller.dto.GenerateUploadUrlRequest;
import com.bombombom.devs.external.video.controller.dto.GenerateUploadUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/videos")
public class VideoController {

    private final S3PreSignedUrlProvider s3PreSignedUrlProvider;

    @GetMapping("/presigned-url")
    public ResponseEntity<GenerateUploadUrlResponse> generateUploadUrl(
        GenerateUploadUrlRequest request) {
        String presignedUrl = s3PreSignedUrlProvider.generatePreSignedGetUrl(request.userId(),
            "a");
        return ResponseEntity.ok().body(GenerateUploadUrlResponse.fromResult(presignedUrl));
    }
}
