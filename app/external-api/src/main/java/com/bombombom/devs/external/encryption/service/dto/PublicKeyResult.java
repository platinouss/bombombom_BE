package com.bombombom.devs.external.encryption.service.dto;

import lombok.Builder;

@Builder
public record PublicKeyResult(
    int id,
    long version,
    String publicKey
) {

    public static PublicKeyResult fromEntry(int id, long version, String publicKey) {
        return PublicKeyResult.builder()
            .id(id)
            .version(version)
            .publicKey(publicKey)
            .build();
    }
}
