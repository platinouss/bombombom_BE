package com.bombombom.devs;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import java.net.URL;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3PreSignedUrlProvider {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public String generatePreSignedGetUrl(Long userId, String fileName) {
        ObjectMetadata metadata = ObjectMetadata.builder()
            .metadata("userId", String.valueOf(userId))
            .build();
        URL presignedUrl = s3Template.createSignedPutURL(bucketName, fileName,
            Duration.ofMinutes(5));
        return presignedUrl.toExternalForm();
    }

}
