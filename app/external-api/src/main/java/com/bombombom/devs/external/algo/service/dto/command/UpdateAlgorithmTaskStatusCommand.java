package com.bombombom.devs.external.algo.service.dto.command;

import com.bombombom.devs.algo.model.vo.TaskStatusUpdateMessage;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;

@Builder
public record UpdateAlgorithmTaskStatusCommand(
    String recordId,
    Long studyId,
    Long userId,
    String baekjoonId,
    Set<Integer> problemRefIds,
    LocalDateTime requestTime
) {

    public static UpdateAlgorithmTaskStatusCommand fromMessage(TaskStatusUpdateMessage message) {
        long requestTimeMillis = Long.parseLong(message.recordId().split("-")[0]);
        LocalDateTime requestTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(requestTimeMillis), ZoneId.systemDefault());
        return UpdateAlgorithmTaskStatusCommand.builder()
            .recordId(message.recordId())
            .studyId(message.studyId())
            .userId(message.userId())
            .baekjoonId(message.baekjoonId())
            .problemRefIds(new HashSet<>(message.redIds()))
            .requestTime(requestTime)
            .build();
    }

}
