package com.bombombom.devs.util.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class MultiValueMapConverter {

    public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object dto) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String, String> convertedMap = objectMapper.convertValue(dto,
                new TypeReference<Map<String, String>>() {
                });
            params.setAll(convertedMap);
            return params;
        } catch (Exception e) {
            throw new IllegalStateException("URL 파라미터 변환 중 오류가 발생했습니다.");
        }
    }
}
