package com.bombombom.devs.external.algo.service.dto.command;

import com.bombombom.devs.algo.model.vo.AssignAlgorithmProblemMessage;
import com.bombombom.devs.core.Spread;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import java.util.Map;
import java.util.Set;
import lombok.Builder;

@Builder
public record AssignAlgorithmProblemCommand(
    Round round,
    Set<String> baekjoonIds,
    Map<AlgoTag, Spread> difficultySpread,
    Map<AlgoTag, Integer> problemCountForEachTag
) {

    public static AssignAlgorithmProblemCommand of(Study study, Round round,
        Map<AlgoTag, Integer> problemCountForEachTag) {
        AlgorithmStudy algorithmStudy = (AlgorithmStudy) study;
        return AssignAlgorithmProblemCommand.builder()
            .round(round)
            .baekjoonIds(study.getBaekjoonIds())
            .difficultySpread(algorithmStudy.getDifficultySpreadMap())
            .problemCountForEachTag(problemCountForEachTag)
            .build();
    }

    public static AssignAlgorithmProblemCommand fromMessage(AssignAlgorithmProblemMessage message,
        Round round) {
        return AssignAlgorithmProblemCommand.builder()
            .round(round)
            .baekjoonIds(message.baekjoonIds())
            .difficultySpread(message.difficultySpread())
            .problemCountForEachTag(message.problemCountForEachTag())
            .build();
    }

    public AssignAlgorithmProblemMessage toVo() {
        return AssignAlgorithmProblemMessage.builder()
            .roundId(round.getId())
            .baekjoonIds(baekjoonIds)
            .difficultySpread(difficultySpread)
            .problemCountForEachTag(problemCountForEachTag)
            .build();
    }

}
