package com.bombombom.devs.algo.model.vo;

import com.bombombom.devs.algo.enums.AlgorithmProblemRequestType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Builder;
import org.springframework.data.redis.connection.stream.MapRecord;

@Builder
public record AlgorithmAssignmentQueueMessage(
    String recordId,
    LocalDateTime requestTime,
    AlgorithmProblemRequestType requestType,
    String fields
) {

    public static AlgorithmAssignmentQueueMessage fromResult(
        MapRecord<String, String, String> message) {
        String recordId = message.getId().toString();
        long requestTimeMillis = Long.parseLong(recordId.split("-")[0]);
        LocalDateTime requestTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(requestTimeMillis), ZoneId.systemDefault());
        return AlgorithmAssignmentQueueMessage.builder()
            .recordId(message.getId().toString())
            .requestTime(requestTime)
            .requestType(AlgorithmProblemRequestType.valueOf(message.getValue().get("type")))
            .fields(message.getValue().get("message"))
            .build();
    }

}
