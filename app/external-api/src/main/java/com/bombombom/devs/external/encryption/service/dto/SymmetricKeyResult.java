package com.bombombom.devs.external.encryption.service.dto;

import javax.crypto.SecretKey;
import lombok.Builder;

@Builder
public record SymmetricKeyResult(
    Long version,
    SecretKey symmetricKey
) {

    public static SymmetricKeyResult fromEntity(Long version, SecretKey secretKey) {
        return SymmetricKeyResult.builder()
            .version(version)
            .symmetricKey(secretKey)
            .build();
    }

}
