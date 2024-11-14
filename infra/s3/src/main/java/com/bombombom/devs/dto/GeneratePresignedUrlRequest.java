package com.bombombom.devs.dto;

import lombok.Builder;

@Builder
public record GeneratePresignedUrlRequest(
    String uploadId,
    Integer partNumber,
    String contentType,
    String objectName,
    Long contentLength
) {

}
