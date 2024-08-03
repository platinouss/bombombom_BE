package com.bombombom.devs.study.repository;

import com.bombombom.devs.core.util.Clock;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamInfo.XInfoGroups;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AlgorithmProblemSolvedHistoryRedisRepository {

    static final String TASK_STATUS_PREFIX = "algo:task:status:";
    static final String TASK_STATUS_UPDATE_TIME_KEY = TASK_STATUS_PREFIX + "update_time";
    static final String TASK_STATUS_UPDATE_KEY = TASK_STATUS_PREFIX + "stream";
    static final String TASK_STATUS_UPDATE_CONSUMER_GROUP = TASK_STATUS_UPDATE_KEY + "-" + "group";
    static final String TASK_STATUS_UPDATE_CONSUMER = TASK_STATUS_UPDATE_KEY + "-" + "service";

    private final Clock clock;
    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;
    private final StreamOperations<String, String, String> streamOperations;

    public String getLastTaskUpdateTime(Long userId) {
        return hashOperations.get(TASK_STATUS_UPDATE_TIME_KEY, String.valueOf(userId));
    }

    public void updateLastTaskUpdateTime(Long userId) {
        Long currentTime = clock.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        hashOperations.put(TASK_STATUS_UPDATE_TIME_KEY, String.valueOf(userId),
            String.valueOf(currentTime));
    }

    public void createConsumerGroup() {
        Boolean streamExists = redisTemplate.hasKey(TASK_STATUS_UPDATE_KEY);
        if (streamExists == null || !streamExists) {
            streamOperations.createGroup(TASK_STATUS_UPDATE_KEY,
                ReadOffset.from("0"), TASK_STATUS_UPDATE_CONSUMER_GROUP);
            return;
        }
        XInfoGroups groups = streamOperations.groups(TASK_STATUS_UPDATE_KEY);
        if (groups.stream()
            .noneMatch(group -> group.groupName().equals(TASK_STATUS_UPDATE_CONSUMER_GROUP))) {
            streamOperations.createGroup(TASK_STATUS_UPDATE_KEY,
                ReadOffset.from("0"), TASK_STATUS_UPDATE_CONSUMER_GROUP);
        }
    }

    public void addMessage(Long userId, String baekjoonId, List<Integer> problemRefIds) {
        Map<String, String> message = Map.of(
            "userId", String.valueOf(userId),
            "baekjoonId", baekjoonId,
            "refId", problemRefIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        streamOperations.add(
            StreamRecords.mapBacked(message).withStreamKey(TASK_STATUS_UPDATE_KEY));
    }

    public Map<String, String> readMessage() {
        StreamReadOptions streamReadOptions = StreamReadOptions.empty().count(1);
        StreamOffset<String> streamOffset = StreamOffset.create(TASK_STATUS_UPDATE_KEY,
            ReadOffset.lastConsumed());
        List<MapRecord<String, String, String>> messages = streamOperations.read(
            Consumer.from(TASK_STATUS_UPDATE_CONSUMER_GROUP, TASK_STATUS_UPDATE_CONSUMER),
            streamReadOptions, streamOffset);
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        MapRecord<String, String, String> message = messages.getFirst();
        Map<String, String> result = new HashMap<>(Map.of("recordId", message.getId().toString()));
        result.putAll(message.getValue());
        return result;
    }

    public void ackMessage(String recordId) {
        streamOperations.acknowledge(TASK_STATUS_UPDATE_KEY, TASK_STATUS_UPDATE_CONSUMER_GROUP,
            recordId);
    }
}
