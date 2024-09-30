package com.bombombom.devs.algo.model.vo;

import com.bombombom.devs.core.Spread;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Builder;

@Builder
public record AssignAlgorithmProblem(
    Long roundId,
    Set<String> baekjoonIds,
    Map<AlgoTag, Spread> difficultySpread,
    Map<AlgoTag, Integer> problemCountForEachTag
) {

    public static AssignAlgorithmProblem of(Study study, AlgorithmStudy algorithmStudy, Round round,
        Map<AlgoTag, Integer> problemCountForEachTag) {
        return AssignAlgorithmProblem.builder()
            .roundId(round.getId())
            .baekjoonIds(new HashSet<>(study.getBaekjoonIds()))
            .difficultySpread(algorithmStudy.getDifficultySpreadMap())
            .problemCountForEachTag(problemCountForEachTag)
            .build();
    }

}
