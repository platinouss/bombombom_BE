package com.bombombom.devs.external.algo.service.dto.command;

import com.bombombom.devs.algo.model.vo.AlgorithmProblemQueueMessage;
import com.bombombom.devs.algo.model.vo.UpdateAlgorithmTaskStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record UpdateAlgorithmTaskStatusCommand(
    String recordId,
    LocalDateTime requestTime,
    Long studyId,
    Long userId,
    String baekjoonId,
    Set<Integer> problemRefIds
) {

    public static UpdateAlgorithmTaskStatusCommand fromMessage(
        AlgorithmProblemQueueMessage message, ObjectMapper objectMapper)
        throws JsonProcessingException {
        UpdateAlgorithmTaskStatus updateAlgorithmTaskStatus = objectMapper.readValue(
            message.fields(), UpdateAlgorithmTaskStatus.class);
        return UpdateAlgorithmTaskStatusCommand.builder()
            .recordId(message.recordId())
            .requestTime(message.requestTime())
            .studyId(updateAlgorithmTaskStatus.studyId())
            .userId(updateAlgorithmTaskStatus.userId())
            .baekjoonId(updateAlgorithmTaskStatus.baekjoonId())
            .problemRefIds(updateAlgorithmTaskStatus.problemRefIds())
            .build();
    }

}
