package com.bombombom.devs.algo.repository;

import static com.bombombom.devs.common.constant.AlgorithmProblemRedisConstant.TASK_STATUS_UPDATE_QUEUE_CONSUMER;
import static com.bombombom.devs.common.constant.AlgorithmProblemRedisConstant.TASK_STATUS_UPDATE_QUEUE_CONSUMER_GROUP;
import static com.bombombom.devs.common.constant.AlgorithmProblemRedisConstant.TASK_STATUS_UPDATE_QUEUE_KEY;
import static com.bombombom.devs.common.constant.AlgorithmProblemRedisConstant.getAlgorithmTaskStatusUpdateKey;

import com.bombombom.devs.algo.model.vo.AlgorithmTaskUpdateStatus;
import com.bombombom.devs.algo.model.vo.PendingMessageInfo;
import com.bombombom.devs.algo.model.vo.TaskStatusUpdateMessage;
import com.bombombom.devs.core.util.Clock;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessages;
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

    private final Clock clock;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;
    private final StreamOperations<String, String, String> streamOperations;

    public AlgorithmTaskUpdateStatus getTaskUpdateStatus(Long studyId, Long userId) {
        String taskUpdateStatus = hashOperations.get(getAlgorithmTaskStatusUpdateKey(studyId),
            String.valueOf(userId));
        return AlgorithmTaskUpdateStatus.fromJson(objectMapper, taskUpdateStatus);
    }

    public Map<Long, AlgorithmTaskUpdateStatus> getTaskUpdateStatuses(Long studyId,
        List<Long> userIds) {
        List<String> taskUpdateStatuses = hashOperations.multiGet(
            getAlgorithmTaskStatusUpdateKey(studyId),
            userIds.stream().map(String::valueOf).toList());
        return IntStream.range(0, userIds.size()).boxed().collect(Collectors.toMap(userIds::get,
            i -> AlgorithmTaskUpdateStatus.fromJson(objectMapper, taskUpdateStatuses.get(i))));
    }

    public void setTaskUpdateInProgress(Long studyId, Long userId) {
        AlgorithmTaskUpdateStatus taskUpdateStatus = AlgorithmTaskUpdateStatus.of(clock, true);
        hashOperations.put(getAlgorithmTaskStatusUpdateKey(studyId), String.valueOf(userId),
            taskUpdateStatus.toJson(objectMapper));
    }

    public void setTaskUpdateCompleted(Long studyId, Long userId) {
        AlgorithmTaskUpdateStatus taskUpdateStatus = AlgorithmTaskUpdateStatus.of(clock, false);
        hashOperations.put(getAlgorithmTaskStatusUpdateKey(studyId), String.valueOf(userId),
            taskUpdateStatus.toJson(objectMapper));
    }

    public void createConsumerGroup() {
        Boolean streamExists = redisTemplate.hasKey(TASK_STATUS_UPDATE_QUEUE_KEY);
        if (streamExists == null || !streamExists) {
            streamOperations.createGroup(TASK_STATUS_UPDATE_QUEUE_KEY,
                ReadOffset.from("0"), TASK_STATUS_UPDATE_QUEUE_CONSUMER_GROUP);
            return;
        }
        XInfoGroups groups = streamOperations.groups(TASK_STATUS_UPDATE_QUEUE_KEY);
        if (groups.stream().noneMatch(
            group -> group.groupName().equals(TASK_STATUS_UPDATE_QUEUE_CONSUMER_GROUP))) {
            streamOperations.createGroup(TASK_STATUS_UPDATE_QUEUE_KEY,
                ReadOffset.from("0"), TASK_STATUS_UPDATE_QUEUE_CONSUMER_GROUP);
        }
    }

    public void addMessage(Long studyId, Long userId, String baekjoonId,
        List<Integer> problemRefIds) {
        Map<String, String> message = Map.of(
            "studyId", String.valueOf(studyId),
            "userId", String.valueOf(userId),
            "baekjoonId", baekjoonId,
            "refId", problemRefIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        streamOperations.add(
            StreamRecords.mapBacked(message).withStreamKey(TASK_STATUS_UPDATE_QUEUE_KEY));
    }

    public TaskStatusUpdateMessage readMessage() {
        StreamReadOptions streamReadOptions = StreamReadOptions.empty().count(1);
        StreamOffset<String> streamOffset = StreamOffset.create(TASK_STATUS_UPDATE_QUEUE_KEY,
            ReadOffset.lastConsumed());
        List<MapRecord<String, String, String>> messages = streamOperations.read(
            Consumer.from(TASK_STATUS_UPDATE_QUEUE_CONSUMER_GROUP,
                TASK_STATUS_UPDATE_QUEUE_CONSUMER),
            streamReadOptions, streamOffset);
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return TaskStatusUpdateMessage.fromResult(messages.getFirst());
    }

    public PendingMessageInfo getOldestPendingMessageInfo() {
        PendingMessages pendingMessages = streamOperations.pending(TASK_STATUS_UPDATE_QUEUE_KEY,
            TASK_STATUS_UPDATE_QUEUE_CONSUMER_GROUP, Range.closed("-", "+"), 1);
        if (pendingMessages.isEmpty()) {
            return null;
        }
        return PendingMessageInfo.fromResult(pendingMessages.get(0));
    }

    public TaskStatusUpdateMessage getOldestPendingMessage(PendingMessageInfo messageInfo) {
        String messageId = messageInfo.recordId();
        MapRecord<String, String, String> message = Objects.requireNonNull(streamOperations.range(
            TASK_STATUS_UPDATE_QUEUE_KEY, Range.closed(messageId, messageId))).getFirst();
        return TaskStatusUpdateMessage.fromResult(message);
    }

    public void ackMessage(String recordId) {
        streamOperations.acknowledge(TASK_STATUS_UPDATE_QUEUE_KEY,
            TASK_STATUS_UPDATE_QUEUE_CONSUMER_GROUP, recordId);
    }
}
