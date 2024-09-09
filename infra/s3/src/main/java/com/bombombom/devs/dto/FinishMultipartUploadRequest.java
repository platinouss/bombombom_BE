package com.bombombom.devs.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record FinishMultipartUploadRequest(
    String objectName,
    String uploadId,
    List<Part> parts
) {

    public record Part(
        int partNumber,
        String eTag
    ) {

    }

}
