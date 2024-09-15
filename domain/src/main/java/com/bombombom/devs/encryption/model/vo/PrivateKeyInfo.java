package com.bombombom.devs.encryption.model.vo;

import java.util.Map;
import lombok.Builder;

@Builder
public record PrivateKeyInfo(
    String privateKey
) {

    public static PrivateKeyInfo fromResult(Map<String, String> result) {
        return PrivateKeyInfo.builder()
            .privateKey(result.get("privateKey"))
            .build();
    }
}
