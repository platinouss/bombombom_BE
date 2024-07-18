package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.algo.model.AlgoTag;
import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.progress.AlgorithmStudyProgress;
import com.bombombom.devs.study.model.StudyType;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record AlgorithmStudyProgressResponse(
    StudyType studyType,
    Map<Long, AlgorithmProblemInfo> problems,
    Map<Long, StudyMemberInfo> users
) implements StudyProgressResponse {

    @Builder
    public record AlgorithmProblemInfo(
        Integer refId,
        AlgoTag tag,
        String title,
        String link,
        Integer difficulty
    ) {

        public static AlgorithmProblemInfo fromResult(AlgorithmProblem algorithmProblem) {
            return AlgorithmProblemInfo.builder()
                .refId(algorithmProblem.getRefId())
                .tag(algorithmProblem.getTag())
                .title(algorithmProblem.getTitle())
                .link(algorithmProblem.getLink())
                .difficulty(algorithmProblem.getDifficulty())
                .build();
        }
    }

    @Builder
    public record StudyMemberInfo(
        String username,
        Map<Long, Boolean> tasks
    ) {

    }

    public static AlgorithmStudyProgressResponse fromResult(StudyProgressResult<?> studyProgress) {
        AlgorithmStudyProgress algorithmStudyProgress = (AlgorithmStudyProgress) studyProgress.getStudyProgress();
        Map<Long, AlgorithmProblemInfo> algorithmProblemInfo = algorithmStudyProgress
            .algorithmProblems().stream()
            .collect(Collectors.toMap(AlgorithmProblem::getId, AlgorithmProblemInfo::fromResult));
        Map<Long, Boolean> studyTask = algorithmStudyProgress.algorithmProblems()
            .stream().collect(Collectors.toMap(AlgorithmProblem::getId, (study) -> false));
        Map<Long, StudyMemberInfo> users = new HashMap<>();
        studyProgress.getStudyMembers().forEach(member -> {
            Map<Long, Boolean> tasks = new HashMap<>(studyTask);
            users.put(member.getId(),
                StudyMemberInfo.builder().username(member.getUsername()).tasks(tasks).build());
        });
        algorithmStudyProgress.histories().forEach(
            history -> users.get(history.getUser().getId()).tasks.put(history.getProblem().getId(),
                true));
        return AlgorithmStudyProgressResponse.builder()
            .studyType(studyProgress.getStudyType())
            .problems(algorithmProblemInfo)
            .users(users)
            .build();
    }
}
