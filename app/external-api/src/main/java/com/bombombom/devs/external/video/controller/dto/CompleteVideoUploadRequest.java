package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.FinishMultipartUploadRequest;
import com.bombombom.devs.dto.FinishMultipartUploadRequest.Part;
import java.util.List;
import lombok.Builder;

@Builder
public record CompleteVideoUploadRequest(
    String uploadId,
    String objectName,
    List<Part> parts
) {

    public FinishMultipartUploadRequest toS3ClientDto() {
        return FinishMultipartUploadRequest.builder()
            .uploadId(uploadId)
            .objectName(objectName)
            .parts(parts)
            .build();
    }

}
