package com.bombombom.devs.dto;

import lombok.Builder;

@Builder
public record InitiateMultipartUploadRequest(
    String objectName,
    String fileType,
    String studyId,
    String userId
) {

}
