package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.InitiateMultipartUploadRequest;

public record InitiateUploadRequest(
    String studyId,
    String userId
) {

    public InitiateMultipartUploadRequest toS3ClientDto() {
        return InitiateMultipartUploadRequest.builder()
            .objectName("task/" + studyId + "/" + userId)
            .studyId(studyId)
            .userId(userId)
            .fileType("video/*")
            .build();
    }

}
