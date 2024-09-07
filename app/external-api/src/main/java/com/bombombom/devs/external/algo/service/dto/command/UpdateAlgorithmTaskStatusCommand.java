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

//    public static UpdateAlgorithmTaskStatusCommand fromMessage(Map<String, String> message) {
//        List<Integer> refIds = Arrays.stream(message.get("refId").split(","))
//            .map(Integer::parseInt).toList();
//        Set<Integer> problemRefIds = new HashSet<>(refIds);
//        String recordId = message.get("recordId");
//        if (recordId.isEmpty()) {
//            throw new IllegalArgumentException("Invalid Redis Stream record.");
//        }
//        long requestTimeMillis = Long.parseLong(message.get("recordId").split("-")[0]);
//        LocalDateTime requestDateTime = LocalDateTime.ofInstant(
//            Instant.ofEpochMilli(requestTimeMillis), ZoneId.systemDefault());
//        return UpdateAlgorithmTaskStatusCommand.builder()
//            .recordId(recordId)
//            .studyId(Long.parseLong(message.get("studyId")))
//            .userId(Long.parseLong(message.get("userId")))
//            .baekjoonId(message.get("baekjoonId"))
//            .problemRefIds(problemRefIds)
//            .requestTime(requestDateTime)
//            .build();
//    }

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
