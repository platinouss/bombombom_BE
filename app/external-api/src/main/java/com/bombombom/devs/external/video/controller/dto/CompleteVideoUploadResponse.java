package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.FinishMultipartUploadResponse;
import lombok.Builder;

@Builder
public record CompleteVideoUploadResponse(
    String bucketName,
    String objectName
) {

    public static CompleteVideoUploadResponse fromS3ClientResponse(
        FinishMultipartUploadResponse response) {
        return CompleteVideoUploadResponse.builder()
            .bucketName(response.bucket())
            .objectName(response.key())
            .build();
    }

}
