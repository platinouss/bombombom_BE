package com.bombombom.devs.algo.repository;

import static com.bombombom.devs.algo.constant.AlgorithmProblemRedisConstant.ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER;
import static com.bombombom.devs.algo.constant.AlgorithmProblemRedisConstant.ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER_GROUP;
import static com.bombombom.devs.algo.constant.AlgorithmProblemRedisConstant.ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY;
import static com.bombombom.devs.algo.constant.AlgorithmProblemRedisConstant.getAlgorithmAssignmentSolvedStatusUpdateKey;

import com.bombombom.devs.algo.enums.AlgorithmProblemRequestType;
import com.bombombom.devs.algo.model.vo.AlgorithmProblemQueueMessage;
import com.bombombom.devs.algo.model.vo.AlgorithmTaskUpdateStatus;
import com.bombombom.devs.algo.model.vo.AssignAlgorithmProblem;
import com.bombombom.devs.algo.model.vo.PendingMessageInfo;
import com.bombombom.devs.algo.model.vo.UpdateAlgorithmTaskStatus;
import com.bombombom.devs.core.util.Clock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
public class AlgorithmProblemRedisQueueRepository {

    private final Clock clock;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;
    private final StreamOperations<String, String, String> streamOperations;

    public AlgorithmTaskUpdateStatus getTaskUpdateStatus(Long studyId, Long userId) {
        String taskUpdateStatus = hashOperations.get(
            getAlgorithmAssignmentSolvedStatusUpdateKey(studyId), String.valueOf(userId));
        return AlgorithmTaskUpdateStatus.fromJson(objectMapper, taskUpdateStatus);
    }

    public Map<Long, AlgorithmTaskUpdateStatus> getTaskUpdateStatuses(Long studyId,
        List<Long> userIds) {
        List<String> taskUpdateStatuses = hashOperations.multiGet(
            getAlgorithmAssignmentSolvedStatusUpdateKey(studyId),
            userIds.stream().map(String::valueOf).toList());
        return IntStream.range(0, userIds.size()).boxed().collect(Collectors.toMap(userIds::get,
            i -> AlgorithmTaskUpdateStatus.fromJson(objectMapper, taskUpdateStatuses.get(i))));
    }

    public void setTaskUpdateInProgress(Long studyId, Long userId) {
        AlgorithmTaskUpdateStatus taskUpdateStatus = AlgorithmTaskUpdateStatus.of(clock, true);
        hashOperations.put(getAlgorithmAssignmentSolvedStatusUpdateKey(studyId),
            String.valueOf(userId), taskUpdateStatus.toJson(objectMapper));
    }

    public void setTaskUpdateCompleted(Long studyId, Long userId) {
        AlgorithmTaskUpdateStatus taskUpdateStatus = AlgorithmTaskUpdateStatus.of(clock, false);
        hashOperations.put(getAlgorithmAssignmentSolvedStatusUpdateKey(studyId),
            String.valueOf(userId), taskUpdateStatus.toJson(objectMapper));
    }

    public void createConsumerGroup() {
        Boolean streamExists = redisTemplate.hasKey(ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY);
        if (streamExists == null || !streamExists) {
            streamOperations.createGroup(ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY,
                ReadOffset.from("0"), ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER_GROUP);
            return;
        }
        XInfoGroups groups = streamOperations.groups(ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY);
        if (groups.stream().noneMatch(
            group -> group.groupName().equals(ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER_GROUP))) {
            streamOperations.createGroup(ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY,
                ReadOffset.from("0"), ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER_GROUP);
        }
    }

    public void addMessage(AssignAlgorithmProblem assignAlgorithmProblem)
        throws JsonProcessingException {
        Map<String, String> message = new HashMap<>();
        message.put("type", AlgorithmProblemRequestType.ASSIGN.name());
        message.put("data", objectMapper.writeValueAsString(assignAlgorithmProblem));
        streamOperations.add(
            StreamRecords.mapBacked(message).withStreamKey(ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY));
    }

    public void addMessage(Long studyId, Long userId, String baekjoonId, Set<Integer> problemRefIds)
        throws JsonProcessingException {
        Map<String, String> message = new HashMap<>();
        message.put("type", AlgorithmProblemRequestType.UPDATE.name());
        message.put("data", objectMapper.writeValueAsString(
            UpdateAlgorithmTaskStatus.of(studyId, userId, baekjoonId, problemRefIds)));
        streamOperations.add(
            StreamRecords.mapBacked(message).withStreamKey(ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY));
    }

    public AlgorithmProblemQueueMessage readMessage() {
        StreamReadOptions streamReadOptions = StreamReadOptions.empty().count(1);
        StreamOffset<String> streamOffset = StreamOffset.create(
            ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY, ReadOffset.lastConsumed());
        List<MapRecord<String, String, String>> messages = streamOperations.read(
            Consumer.from(ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER_GROUP,
                ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER), streamReadOptions, streamOffset);
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return AlgorithmProblemQueueMessage.fromResult(messages.getFirst());
    }

    public PendingMessageInfo getOldestPendingMessageInfo() {
        PendingMessages pendingMessages = streamOperations.pending(
            ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY, ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER_GROUP,
            Range.closed("-", "+"), 1);
        if (pendingMessages.isEmpty()) {
            return null;
        }
        return PendingMessageInfo.fromResult(pendingMessages.get(0));
    }

    public AlgorithmProblemQueueMessage getOldestPendingMessage(PendingMessageInfo messageInfo) {
        String messageId = messageInfo.recordId();
        MapRecord<String, String, String> message = Objects.requireNonNull(streamOperations.range(
            ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY, Range.closed(messageId, messageId))).getFirst();
        return AlgorithmProblemQueueMessage.fromResult(message);
    }

    public void ackMessage(String recordId) {
        streamOperations.acknowledge(ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY,
            ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER_GROUP, recordId);
    }
}
