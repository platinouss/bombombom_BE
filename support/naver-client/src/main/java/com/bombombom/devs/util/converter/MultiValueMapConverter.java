package com.bombombom.devs.util.converter;

import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ServerInternalException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
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
            log.error("convert fail: {}", e.getMessage());
            throw new ServerInternalException(ErrorCode.URL_PARAM_CONVERT_FAIL);
        }
    }
}
