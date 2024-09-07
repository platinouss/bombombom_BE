package com.bombombom.devs.algo.model.vo;

import java.time.Duration;
import lombok.Builder;
import org.springframework.data.redis.connection.stream.PendingMessage;

@Builder
public record PendingMessageInfo(
    String recordId,
    String consumerGroup,
    String consumerName,
    Duration elapsedTime,
    Long totalDeliveryCount
) {

    public static PendingMessageInfo fromResult(PendingMessage message) {
        return PendingMessageInfo.builder()
            .recordId(message.getIdAsString())
            .consumerGroup(message.getGroupName())
            .consumerName(message.getConsumerName())
            .elapsedTime(message.getElapsedTimeSinceLastDelivery())
            .totalDeliveryCount(message.getTotalDeliveryCount())
            .build();
    }

}
