package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.GeneratePresignedUrlResponse;
import lombok.Builder;

@Builder
public record GenerateUploadUrlResponse(
    String presignedUrl
) {

    public static GenerateUploadUrlResponse fromS3ClientResponse(
        GeneratePresignedUrlResponse response) {
        return GenerateUploadUrlResponse.builder()
            .presignedUrl(response.presignedUrl())
            .build();
    }

}
