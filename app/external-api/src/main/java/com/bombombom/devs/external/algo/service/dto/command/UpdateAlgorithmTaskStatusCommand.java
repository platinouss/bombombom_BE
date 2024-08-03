package com.bombombom.devs.external.algo.service.dto.command;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;

@Builder
public record UpdateAlgorithmTaskStatusCommand(
    String recordId,
    Long userId,
    String baekjoonId,
    Set<Integer> problemRefIds,
    LocalDateTime requestTime
) {

    public static UpdateAlgorithmTaskStatusCommand fromMessage(Map<String, String> message) {
        List<Integer> refIds = Arrays.stream(message.get("refId").split(","))
            .map(Integer::parseInt).toList();
        Set<Integer> problemRefIds = new HashSet<>(refIds);
        String recordId = message.get("recordId");
        if (recordId.isEmpty()) {
            throw new IllegalArgumentException("Invalid Redis Stream record.");
        }
        long requestTimeMillis = Long.parseLong(message.get("recordId").split("-")[0]);
        LocalDateTime requestDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(requestTimeMillis), ZoneId.systemDefault());
        return UpdateAlgorithmTaskStatusCommand.builder()
            .recordId(recordId)
            .userId(Long.parseLong(message.get("userId")))
            .baekjoonId(message.get("baekjoonId"))
            .problemRefIds(problemRefIds)
            .requestTime(requestDateTime)
            .build();
    }

}
