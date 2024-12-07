package com.bombombom.devs.algo.model.vo;

import com.bombombom.devs.core.Spread;
import com.bombombom.devs.core.enums.AlgoTag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Set;
import lombok.Builder;

@Builder
public record AssignAlgorithmProblemMessage(
    Long roundId,
    Set<String> baekjoonIds,
    Map<AlgoTag, Spread> difficultySpread,
    Map<AlgoTag, Integer> problemCountForEachTag
) {

    public static AssignAlgorithmProblemMessage fromRecord(AlgorithmAssignmentQueueMessage record,
        ObjectMapper objectMapper) throws JsonProcessingException {
        return objectMapper.readValue(record.fields(), AssignAlgorithmProblemMessage.class);
    }
}
