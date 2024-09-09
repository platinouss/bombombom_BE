package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.InitiateMultipartUploadResponse;
import lombok.Builder;

@Builder
public record InitiateUploadResponse(
    String uploadId
) {

    public static InitiateUploadResponse fromS3ClientResponse(
        InitiateMultipartUploadResponse response) {
        return InitiateUploadResponse.builder()
            .uploadId(response.uploadId())
            .build();
    }

}
