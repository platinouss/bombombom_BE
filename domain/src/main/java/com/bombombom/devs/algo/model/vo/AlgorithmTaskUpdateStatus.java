package com.bombombom.devs.algo.model.vo;

import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ServerInternalException;
import com.bombombom.devs.core.util.Clock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZoneOffset;
import lombok.Builder;

@Builder
public record AlgorithmTaskUpdateStatus(
    Boolean isUpdating,
    Long statusUpdatedAt
) {

    public static AlgorithmTaskUpdateStatus of(Clock clock, boolean isUpdating) {
        Long currentTime = clock.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        return new AlgorithmTaskUpdateStatus(isUpdating, currentTime);
    }

    public static AlgorithmTaskUpdateStatus fromJson(ObjectMapper objectMapper,
        String taskUpdateStatus) {
        if (taskUpdateStatus == null) {
            return new AlgorithmTaskUpdateStatus(false, null);
        }
        try {
            return objectMapper.readValue(taskUpdateStatus, AlgorithmTaskUpdateStatus.class);
        } catch (JsonProcessingException e) {
            throw new ServerInternalException(ErrorCode.JSON_CONVERSION_FAIL);
        }
    }

    public String toJson(ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new ServerInternalException(ErrorCode.JSON_CONVERSION_FAIL);
        }
    }
}
