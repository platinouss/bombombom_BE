package com.bombombom.devs.external.video.controller;

import com.bombombom.devs.S3MultipartUploadClient;
import com.bombombom.devs.dto.FinishMultipartUploadResponse;
import com.bombombom.devs.dto.GeneratePresignedUrlResponse;
import com.bombombom.devs.dto.InitiateMultipartUploadResponse;
import com.bombombom.devs.external.video.controller.dto.CompleteVideoUploadRequest;
import com.bombombom.devs.external.video.controller.dto.CompleteVideoUploadResponse;
import com.bombombom.devs.external.video.controller.dto.GenerateUploadUrlRequest;
import com.bombombom.devs.external.video.controller.dto.GenerateUploadUrlResponse;
import com.bombombom.devs.external.video.controller.dto.InitiateUploadRequest;
import com.bombombom.devs.external.video.controller.dto.InitiateUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/videos")
public class VideoController {

    private final S3MultipartUploadClient s3MultipartUploadClient;

    @PostMapping("/initiate-upload")
    public ResponseEntity<InitiateUploadResponse> initiateUpload(
        @RequestBody InitiateUploadRequest request) {
        InitiateMultipartUploadResponse response = s3MultipartUploadClient.initiate(
            request.toS3ClientDto());
        return ResponseEntity.ok(InitiateUploadResponse.fromS3ClientResponse(response));
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<GenerateUploadUrlResponse> generateMultipartUploadUrl(
        @RequestBody GenerateUploadUrlRequest request) {
        GeneratePresignedUrlResponse response = s3MultipartUploadClient.generatePresignedUrl(
            request.toS3ClientDto());
        return ResponseEntity.ok().body(GenerateUploadUrlResponse.fromS3ClientResponse(response));
    }

    @PostMapping("/complete-upload")
    public ResponseEntity<CompleteVideoUploadResponse> completeVideoUpload(
        @RequestBody CompleteVideoUploadRequest request) {
        FinishMultipartUploadResponse response = s3MultipartUploadClient.finishMultipartUpload(
            request.toS3ClientDto());
        return ResponseEntity.ok(CompleteVideoUploadResponse.fromS3ClientResponse(response));
    }
}
