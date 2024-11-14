package com.bombombom.devs.dto;

import lombok.Builder;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;

@Builder
public record InitiateMultipartUploadResponse(
    String uploadId
) {

    public static InitiateMultipartUploadResponse fromResult(CreateMultipartUploadResponse result) {
        return InitiateMultipartUploadResponse.builder()
            .uploadId(result.uploadId())
            .build();
    }

}
