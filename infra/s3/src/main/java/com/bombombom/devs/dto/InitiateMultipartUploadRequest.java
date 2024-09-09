package com.bombombom.devs.dto;

import lombok.Builder;

@Builder
public record InitiateMultipartUploadRequest(
    String originalFileName,
    String fileType,
    String studyId,
    String userId
) {

}
