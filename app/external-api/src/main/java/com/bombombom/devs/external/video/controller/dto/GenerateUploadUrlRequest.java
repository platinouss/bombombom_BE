package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.GeneratePresignedUrlRequest;

public record GenerateUploadUrlRequest(
    String uploadId,
    Integer partNumber,
    String objectName,
    Long partSize
) {

    public GeneratePresignedUrlRequest toS3ClientDto() {
        return GeneratePresignedUrlRequest.builder()
            .uploadId(uploadId)
            .partNumber(partNumber)
            .contentType("video/*")
            .objectName(objectName)
            .contentLength(partSize)
            .build();
    }

}
