package com.bombombom.devs.dto;

import lombok.Builder;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;

@Builder
public record GeneratePresignedUrlResponse(
    String presignedUrl
) {

    public static GeneratePresignedUrlResponse fromResult(PresignedUploadPartRequest result) {
        return GeneratePresignedUrlResponse.builder()
            .presignedUrl(result.url().toString())
            .build();
    }

}
