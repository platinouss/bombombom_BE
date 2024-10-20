package com.bombombom.devs.external.encryption.controller.dto;

import com.bombombom.devs.external.encryption.service.dto.PublicKeyResult;
import lombok.Builder;

@Builder
public record ProvidePublicKeyResponse(
    int id,
    long version,
    String publicKey
) {

    public static ProvidePublicKeyResponse fromResult(PublicKeyResult result) {
        return ProvidePublicKeyResponse.builder()
            .id(result.id())
            .version(result.version())
            .publicKey(result.publicKey())
            .build();
    }
}
