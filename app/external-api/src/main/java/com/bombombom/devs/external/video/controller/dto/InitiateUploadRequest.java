package com.bombombom.devs.external.video.controller.dto;

import com.bombombom.devs.dto.InitiateMultipartUploadRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record InitiateUploadRequest(
    @Min(0) long studyId,
    @Min(0) long userId,
    @Pattern(regexp = "^video/.+", message = "비디오 파일의 MIME 타입만 가능합니다.") String fileType
) {

    public InitiateMultipartUploadRequest toS3ClientDto() {
        return InitiateMultipartUploadRequest.builder()
            .objectName("task/" + studyId + "/" + userId)
            .studyId(String.valueOf(studyId))
            .userId(String.valueOf(userId))
            .fileType(fileType)
            .build();
    }

}
