package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.external.study.controller.dto.request.EditAssignmentRequest.AssignmentInfo;
import com.bombombom.devs.external.study.service.dto.result.AssignmentResult;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.progress.BookStudyProgress;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

// TODO: 서적 스터디 진행 현황 응답 필드 구성 및 fromResult 메서드 수정
@Builder
public record BookStudyProgressResponse(
    Integer idx,
    LocalDate startDate,
    LocalDate endDate,
    Map<Long, AssignmentInfo> assignments,
    Map<Long, MemberAndSubmissionInfo> users
) implements StudyProgressResponse {


    public record MemberAndSubmissionInfo(
        String username,
        Long assignmentId,
        List<Long> videoIds,
        List<Long> problemIds
    ) {


        public static MemberAndSubmissionInfo fromResult(UserProfileResult member,
            Long assignmentId, List<Long> videoIds, List<Long> problemIds) {
            return new MemberAndSubmissionInfo(member.username(), assignmentId, videoIds,
                problemIds);
        }
    }


    public static BookStudyProgressResponse fromResult(StudyProgressResult studyProgress) {
        BookStudyProgress bookStudyProgress = (BookStudyProgress) studyProgress.studyProgress();

        Map<Long, AssignmentInfo> assignments = bookStudyProgress.assignments().stream()
            .collect(Collectors.toMap(AssignmentResult::id, AssignmentInfo::fromResult));

        Map<Long, List<Long>> videoIdsMap = bookStudyProgress.videoIdsMap();
        Map<Long, List<Long>> problemIdsMap = bookStudyProgress.problemIdsMap();
        Map<Long, Long> assignmentIdMap = bookStudyProgress.assignmentIdMap();
        Map<Long, MemberAndSubmissionInfo> users = new HashMap<>();

        studyProgress.members().forEach(member -> {

            if (!assignmentIdMap.containsKey(member.id())) {
                throw new NotFoundException(ErrorCode.USER_ASSIGNMENT_NOT_FOUND);
            }
            Long assignmentId = assignmentIdMap.get(member.id());
            List<Long> videoIds = videoIdsMap.getOrDefault(member.id(), List.of());
            List<Long> problemIds = problemIdsMap.getOrDefault(member.id(), List.of());

            users.put(member.id(),
                MemberAndSubmissionInfo.fromResult(member, assignmentId, videoIds, problemIds));
        });

        return BookStudyProgressResponse.builder()
            .idx(bookStudyProgress.round().idx())
            .startDate(bookStudyProgress.round().startDate())
            .endDate(bookStudyProgress.round().endDate())
            .assignments(assignments)
            .users(users)
            .build();


    }

}
