package com.bombombom.devs.external.algo.service.dto.command;

import com.bombombom.devs.algo.model.vo.AlgorithmProblemQueueMessage;
import com.bombombom.devs.algo.model.vo.AssignAlgorithmProblem;
import com.bombombom.devs.core.Spread;
import com.bombombom.devs.core.enums.AlgoTag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.Builder;

@Builder
public record AssignAlgorithmProblemCommand(
    String recordId,
    LocalDateTime requestTime,
    Long roundId,
    Set<String> baekjoonIds,
    Map<AlgoTag, Spread> difficultySpread,
    Map<AlgoTag, Integer> problemCountForEachTag
) {

    public static AssignAlgorithmProblemCommand fromMessage(AlgorithmProblemQueueMessage message,
        ObjectMapper objectMapper) throws JsonProcessingException {
        AssignAlgorithmProblem assignAlgorithmProblem = objectMapper.readValue(
            message.fields(), AssignAlgorithmProblem.class);
        return AssignAlgorithmProblemCommand.builder()
            .recordId(message.recordId())
            .requestTime(message.requestTime())
            .roundId(assignAlgorithmProblem.roundId())
            .baekjoonIds(assignAlgorithmProblem.baekjoonIds())
            .difficultySpread(assignAlgorithmProblem.difficultySpread())
            .problemCountForEachTag(assignAlgorithmProblem.problemCountForEachTag())
            .build();
    }

}
