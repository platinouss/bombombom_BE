package com.bombombom.devs.dto;

import lombok.Builder;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;

@Builder
public record FinishMultipartUploadResponse(
    String bucket,
    String key
) {

    public static FinishMultipartUploadResponse fromResult(CompleteMultipartUploadResponse result) {
        return FinishMultipartUploadResponse.builder()
            .bucket(result.bucket())
            .key(result.key())
            .build();
    }
}
