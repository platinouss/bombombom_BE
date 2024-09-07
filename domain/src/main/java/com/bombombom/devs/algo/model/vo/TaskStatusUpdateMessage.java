package com.bombombom.devs.algo.model.vo;

import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import org.springframework.data.redis.connection.stream.MapRecord;

@Builder
public record TaskStatusUpdateMessage(
    String recordId,
    String baekjoonId,
    Long studyId,
    Long userId,
    List<Integer> redIds
) {

    public static TaskStatusUpdateMessage fromResult(MapRecord<String, String, String> message) {
        return TaskStatusUpdateMessage.builder()
            .recordId(message.getId().toString())
            .baekjoonId(message.getValue().get("baekjoonId"))
            .studyId(Long.parseLong(message.getValue().get("studyId")))
            .userId(Long.parseLong(message.getValue().get("userId")))
            .redIds(Arrays.stream(message.getValue().get("refId").split(","))
                .map(Integer::parseInt).toList())
            .build();
    }

}
