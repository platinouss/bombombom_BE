package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.FinishMultipartUploadRequest;
import com.bombombom.devs.dto.FinishMultipartUploadRequest.Part;
import java.util.List;
import lombok.Builder;

@Builder
public record CompleteVideoUploadRequest(
    String uploadId,
    List<Part> parts,
    long studyId,
    long userId
) {

    public FinishMultipartUploadRequest toS3ClientDto() {
        return FinishMultipartUploadRequest.builder()
            .uploadId(uploadId)
            .objectName("task/" + studyId + "/" + userId)
            .parts(parts)
            .build();
    }

}
