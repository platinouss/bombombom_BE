package com.bombombom.devs.external.encryption.service.dto;

import com.bombombom.devs.encryption.model.vo.PrivateKeyInfo;
import lombok.Builder;

@Builder
public record PrivateKeyResult(
    int id,
    Double version,
    String privateKey
) {

    public static PrivateKeyResult fromEntry(int id, double version,
        PrivateKeyInfo privateKeyInfo) {
        return PrivateKeyResult.builder()
            .id(id)
            .version(version)
            .privateKey(privateKeyInfo.privateKey())
            .build();
    }

}
