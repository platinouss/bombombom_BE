package com.bombombom.devs.external.encryption.service.dto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.Builder;

@Builder
public record AsymmetricKeyResult(
    long version,
    KeyPair keyPair
) {

    public static AsymmetricKeyResult fromEntity(long version, PublicKey publicKey,
        PrivateKey privateKey) {
        KeyPair asymmetricKeyPair = new KeyPair(publicKey, privateKey);
        return AsymmetricKeyResult.builder()
            .version(version)
            .keyPair(asymmetricKeyPair)
            .build();
    }

}
