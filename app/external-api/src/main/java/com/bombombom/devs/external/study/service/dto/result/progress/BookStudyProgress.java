package com.bombombom.devs.external.study.service.dto.result.progress;

import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.external.study.service.dto.result.AssignmentResult;
import com.bombombom.devs.external.study.service.dto.result.RoundResult;
import com.bombombom.devs.study.model.Assignment;
import com.bombombom.devs.study.model.Problem;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.UserAssignment;
import com.bombombom.devs.study.model.Video;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record BookStudyProgress(
    RoundResult round,
    List<AssignmentResult> assignments,
    Map<Long, Long> assignmentIdMap,
    Map<Long, List<Long>> videoIdsMap,
    Map<Long, List<Long>> problemIdsMap

) implements StudyProgress {


    public static BookStudyProgress fromEntity(
        Round round, List<Assignment> assignments,
        List<UserAssignment> userAssignments,
        List<Problem> problems,
        List<Video> videos
    ) {
        Map<Long, Long> assignmentIdMap =
            userAssignments.stream().collect(Collectors.toMap(
                UserAssignment::getUserId, UserAssignment::getAssignmentId
            ));

        Map<Long, List<Long>> videoIdsMap = new HashMap<>();
        videos.forEach(video -> {
            Long userId = video.getUploader().getId();
            Long assignmentId = video.getAssignment().getId();

            if (!assignmentIdMap.containsKey(userId)) {
                throw new NotFoundException(ErrorCode.USER_ASSIGNMENT_NOT_FOUND);
            }

            if (!assignmentId.equals(assignmentIdMap.get(userId))) {
                throw new BusinessRuleException(ErrorCode.VIDEO_ASSIGNMENT_ID_NOT_MATCH);
            }

            videoIdsMap.merge(userId,
                new ArrayList<>(List.of(video.getId())),
                (oldValue, value) -> {
                    oldValue.addAll(value);
                    return oldValue;
                });

        });

        Map<Long, List<Long>> problemIdsMap = new HashMap<>();
        problems.forEach(problem -> {
            Long userId = problem.getExaminer().getId();
            Long assignmentId = problem.getAssignment().getId();

            if (!assignmentIdMap.containsKey(userId)) {
                throw new NotFoundException(ErrorCode.USER_ASSIGNMENT_NOT_FOUND);
            }
            if (!assignmentId.equals(assignmentIdMap.get(userId))) {
                throw new BusinessRuleException(ErrorCode.PROBLEM_ASSIGNMENT_ID_NOT_MATCH);
            }

            problemIdsMap.merge(userId,
                new ArrayList<>(List.of(problem.getId())),
                (oldValue, value) -> {
                    oldValue.addAll(value);
                    return oldValue;
                });
        });

        return BookStudyProgress.builder()
            .round(RoundResult.fromEntity(round))
            .assignments(assignments.stream().map(AssignmentResult::fromEntity).toList())
            .assignmentIdMap(assignmentIdMap)
            .videoIdsMap(videoIdsMap)
            .problemIdsMap(problemIdsMap)
            .build();
    }

}
