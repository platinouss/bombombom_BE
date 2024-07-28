package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.external.algo.service.dto.result.AlgorithmProblemResult;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.progress.AlgorithmStudyProgress;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record AlgorithmStudyProgressResponse(
    Integer idx,
    LocalDate startDate,
    LocalDate endDate,
    Map<Long, AlgorithmProblemInfo> problems,
    Map<Long, AlgorithmStudyTaskStatusResponse> users
) implements StudyProgressResponse {

    @Builder
    public record AlgorithmProblemInfo(
        Integer refId,
        AlgoTag tag,
        String title,
        String link,
        Integer difficulty
    ) {

        public static AlgorithmProblemInfo fromResult(AlgorithmProblemResult result) {
            return AlgorithmProblemInfo.builder()
                .refId(result.refId())
                .tag(result.tag())
                .title(result.title())
                .link(result.link())
                .difficulty(result.difficulty())
                .build();
        }
    }

    public static AlgorithmStudyProgressResponse fromResult(StudyProgressResult studyProgress) {
        AlgorithmStudyProgress algorithmStudyProgress = (AlgorithmStudyProgress) studyProgress.studyProgress();
        Map<Long, AlgorithmProblemInfo> algorithmProblemInfo = algorithmStudyProgress
            .algorithmProblems().stream().collect(
                Collectors.toMap(AlgorithmProblemResult::id, AlgorithmProblemInfo::fromResult));
        Map<Long, Boolean> studyTask = algorithmStudyProgress.algorithmProblems()
            .stream().collect(Collectors.toMap(AlgorithmProblemResult::id, (study) -> false));
        Map<Long, AlgorithmStudyTaskStatusResponse> users = new HashMap<>();
        studyProgress.members().forEach(member -> users.put(member.id(),
            AlgorithmStudyTaskStatusResponse.fromResult(member, new HashMap<>(studyTask))));
        algorithmStudyProgress.histories()
            .forEach(history -> users.get(history.userId()).tasks().put(history.problemId(), true));
        return AlgorithmStudyProgressResponse.builder()
            .idx(algorithmStudyProgress.round().idx())
            .startDate(algorithmStudyProgress.round().startDate())
            .endDate(algorithmStudyProgress.round().endDate())
            .problems(algorithmProblemInfo)
            .users(users)
            .build();
    }
}
