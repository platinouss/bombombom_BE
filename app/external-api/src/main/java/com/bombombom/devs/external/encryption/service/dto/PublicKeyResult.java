package com.bombombom.devs.external.encryption.service.dto;

import com.bombombom.devs.encryption.model.vo.PublicKeyInfo;
import lombok.Builder;

@Builder
public record PublicKeyResult(
    int id,
    Double version,
    String publicKey
) {

    public static PublicKeyResult fromEntry(int id, double version, PublicKeyInfo keyPairInfo) {
        return PublicKeyResult.builder()
            .id(id)
            .version(version)
            .publicKey(keyPairInfo.publicKey())
            .build();
    }
}
