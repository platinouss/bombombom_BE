package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.GeneratePresignedUrlRequest;

public record GenerateUploadUrlRequest(
    String uploadId,
    Integer partNumber,
    Long partSize,
    String studyId,
    String userId
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
