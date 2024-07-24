package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.algo.model.AlgoTag;
import com.bombombom.devs.algo.model.AlgorithmProblem;
import lombok.Builder;

@Builder
public record AlgorithmProblemResult(
    Long id,
    Integer refId,
    AlgoTag tag,
    String title,
    String link,
    Integer difficulty
) {

    public static AlgorithmProblemResult fromEntity(AlgorithmProblem algorithmProblem) {
        return AlgorithmProblemResult.builder()
            .id(algorithmProblem.getId())
            .refId(algorithmProblem.getRefId())
            .tag(algorithmProblem.getTag())
            .title(algorithmProblem.getTitle())
            .link(algorithmProblem.getLink())
            .difficulty(algorithmProblem.getDifficulty())
            .build();
    }
}
