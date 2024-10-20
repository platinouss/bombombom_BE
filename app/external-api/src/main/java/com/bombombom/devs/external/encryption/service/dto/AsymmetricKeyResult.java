package com.bombombom.devs.external.encryption.service.dto;

import java.security.KeyPair;
import lombok.Builder;

@Builder
public record AsymmetricKeyResult(
    long version,
    KeyPair keyPair
) {

    public static AsymmetricKeyResult fromEntity(long version, KeyPair keyPair) {
        return AsymmetricKeyResult.builder()
            .version(version)
            .keyPair(keyPair)
            .build();
    }

}
