package com.bombombom.devs.algo.model.vo;

import com.bombombom.devs.core.util.Clock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZoneOffset;

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
            AlgorithmTaskUpdateStatus status = objectMapper.readValue(taskUpdateStatus,
                AlgorithmTaskUpdateStatus.class);
            if (status.isUpdating == null || status.statusUpdatedAt == null) {
                throw new IllegalArgumentException("Invalid task status update record");
            }
            return status;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to convert JSON string to object");
        }
    }

    public String toJson(ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to convert object to JSON string");
        }
    }
}
