package com.bombombom.devs.algo.model.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import lombok.Builder;

@Builder
public record UpdateAlgorithmTaskStatusMessage(
    Long userId,
    Long studyId,
    Set<Long> problemIds
) {

    public static UpdateAlgorithmTaskStatusMessage fromRecord(
        AlgorithmAssignmentQueueMessage record,
        ObjectMapper objectMapper) throws JsonProcessingException {
        return objectMapper.readValue(record.fields(), UpdateAlgorithmTaskStatusMessage.class);
    }
}
