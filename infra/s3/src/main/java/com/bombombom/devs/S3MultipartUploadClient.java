package com.bombombom.devs;

import com.bombombom.devs.dto.FinishMultipartUploadRequest;
import com.bombombom.devs.dto.FinishMultipartUploadResponse;
import com.bombombom.devs.dto.GeneratePresignedUrlRequest;
import com.bombombom.devs.dto.GeneratePresignedUrlResponse;
import com.bombombom.devs.dto.InitiateMultipartUploadRequest;
import com.bombombom.devs.dto.InitiateMultipartUploadResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3MultipartUploadClient {

    private static final Long PRESIGNED_URL_VALID_DURATION_MINUTES = 10L;

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public InitiateMultipartUploadResponse initiate(InitiateMultipartUploadRequest request) {
        Map<String, String> metadata = Map.of("studyId", request.studyId(), "userId",
            request.userId());
        CreateMultipartUploadRequest multipartUploadRequest = CreateMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(request.objectName())
            .contentType(request.fileType())
            .metadata(metadata)
            .build();
        return InitiateMultipartUploadResponse.fromResult(
            s3Client.createMultipartUpload(multipartUploadRequest));
    }

    public GeneratePresignedUrlResponse generatePresignedUrl(GeneratePresignedUrlRequest request) {
        try (S3Presigner presigner = S3Presigner.create()) {
            Duration expirationTime = Duration.ofMinutes(PRESIGNED_URL_VALID_DURATION_MINUTES);
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(request.objectName())
                .uploadId(request.uploadId())
                .partNumber(request.partNumber())
                .contentLength(request.contentLength())
                .build();
            UploadPartPresignRequest presignRequest = UploadPartPresignRequest.builder()
                .signatureDuration(expirationTime)
                .uploadPartRequest(uploadPartRequest)
                .build();
            PresignedUploadPartRequest presignedRequest = presigner.presignUploadPart(
                presignRequest);
            return GeneratePresignedUrlResponse.fromResult(presignedRequest);
        }
    }

    public FinishMultipartUploadResponse finishMultipartUpload(
        FinishMultipartUploadRequest request) {
        List<CompletedPart> completedParts = request.parts().stream()
            .map(part -> CompletedPart.builder()
                .partNumber(part.partNumber())
                .eTag(part.eTag())
                .build()
            ).toList();
        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(request.objectName())
            .uploadId(request.uploadId())
            .multipartUpload(multipartUpload -> multipartUpload.parts(completedParts))
            .build();
        CompleteMultipartUploadResponse response = s3Client.completeMultipartUpload(
            completeMultipartUploadRequest);
        return FinishMultipartUploadResponse.fromResult(response);
    }
}
