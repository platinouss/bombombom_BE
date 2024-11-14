package com.bombombom.devs.dto;

import lombok.Builder;

@Builder
public record IsUploadCompleteRequest(
    String objectName
) {

    public static IsUploadCompleteRequest of(Long studyId, Long assignmentId) {
        return IsUploadCompleteRequest.builder()
            .objectName("task/" + studyId + "/" + assignmentId)
            .build();
    }
}
