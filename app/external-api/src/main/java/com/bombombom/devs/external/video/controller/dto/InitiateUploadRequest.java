package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.InitiateMultipartUploadRequest;

public record InitiateUploadRequest(
    String originalFileName,
    String studyId,
    String userId
) {

    public InitiateMultipartUploadRequest toS3ClientDto() {
        return InitiateMultipartUploadRequest.builder()
            .originalFileName(originalFileName)
            .studyId(studyId)
            .userId(userId)
            .fileType("video/*")
            .build();
    }

}
