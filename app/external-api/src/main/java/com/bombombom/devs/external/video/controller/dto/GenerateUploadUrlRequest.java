package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.GeneratePresignedUrlRequest;
import jakarta.validation.constraints.Min;

public record GenerateUploadUrlRequest(
    String uploadId,
    @Min(0) int partNumber,
    long partSize,
    @Min(0) long studyId,
    @Min(0) long userId
) {

    public GeneratePresignedUrlRequest toS3ClientDto() {
        return GeneratePresignedUrlRequest.builder()
            .uploadId(uploadId)
            .partNumber(partNumber)
            .contentType("video/*")
            .objectName("task/" + studyId + "/" + userId)
            .contentLength(partSize)
            .build();
    }

}
