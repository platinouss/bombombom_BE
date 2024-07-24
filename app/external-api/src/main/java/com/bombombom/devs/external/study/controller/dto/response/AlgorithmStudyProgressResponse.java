package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.algo.model.AlgoTag;
import com.bombombom.devs.algo.model.AlgorithmProblem;
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
    Map<Long, MemberInfo> users
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
    public record MemberInfo(
        String username,
        Map<Long, Boolean> tasks
    ) {

    }

    public static AlgorithmStudyProgressResponse fromResult(StudyProgressResult studyProgress) {
        AlgorithmStudyProgress algorithmStudyProgress = (AlgorithmStudyProgress) studyProgress.studyProgress();
        Map<Long, AlgorithmProblemInfo> algorithmProblemInfo = algorithmStudyProgress
            .algorithmProblems().stream().collect(
                Collectors.toMap(AlgorithmProblem::getId, AlgorithmProblemInfo::fromResult));
        Map<Long, Boolean> studyTask = algorithmStudyProgress.algorithmProblems()
            .stream().collect(Collectors.toMap(AlgorithmProblem::getId, (study) -> false));
        Map<Long, MemberInfo> users = new HashMap<>();
        studyProgress.members().forEach(member -> {
            Map<Long, Boolean> tasks = new HashMap<>(studyTask);
            users.put(member.getId(),
                MemberInfo.builder().username(member.getUsername()).tasks(tasks).build());
        });
        algorithmStudyProgress.histories().forEach(history -> users.get(history.getUser().getId())
            .tasks.put(history.getProblem().getId(), true));
        return AlgorithmStudyProgressResponse.builder()
            .idx(algorithmStudyProgress.round().getIdx())
            .startDate(algorithmStudyProgress.round().getStartDate())
            .endDate(algorithmStudyProgress.round().getEndDate())
            .problems(algorithmProblemInfo)
            .users(users)
            .build();
    }
}
