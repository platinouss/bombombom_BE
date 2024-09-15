package com.bombombom.devs.encryption.model.vo;

import java.util.Map;
import lombok.Builder;

@Builder
public record PublicKeyInfo(
    Double version,
    String publicKey
) {

    public static PublicKeyInfo fromResult(Map<String, String> result) {
        return PublicKeyInfo.builder()
            .publicKey(result.get("publicKey"))
            .build();
    }
}
