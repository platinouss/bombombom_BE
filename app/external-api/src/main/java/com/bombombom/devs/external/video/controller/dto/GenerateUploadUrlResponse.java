package com.bombombom.devs.external.video.controller.dto;

import lombok.Builder;

@Builder
public record GenerateUploadUrlResponse(
    String presignedUrl
) {

    public static GenerateUploadUrlResponse fromResult(String presignedUrl) {
        return GenerateUploadUrlResponse.builder()
            .presignedUrl(presignedUrl)
            .build();
    }

}
