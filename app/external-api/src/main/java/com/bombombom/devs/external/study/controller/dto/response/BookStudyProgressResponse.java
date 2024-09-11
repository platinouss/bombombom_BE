package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.study.controller.dto.request.EditAssignmentRequest.AssignmentInfo;
import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import com.bombombom.devs.external.study.service.dto.result.progress.BookStudyProgress;
import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;

// TODO: 서적 스터디 진행 현황 응답 필드 구성 및 fromResult 메서드 수정
@Builder
public record BookStudyProgressResponse(
    Integer idx,
    LocalDate startDate,
    LocalDate endDate,
    Map<Long, AssignmentInfo> assignments,
    Map<Long, MemberAndReportInfo> users
) implements StudyProgressResponse {


    @Builder
    public record QuizInfo(
        String link
    ) {

    }

    @Builder
    public record VideoInfo(
        String link
    ) {

    }

    public record MemberAndReportInfo(
        String username,
        Long assignmentId,
        VideoInfo video,
        QuizInfo quiz
    ) {

    }


    public static BookStudyProgressResponse fromResult(StudyProgressResult studyProgress) {
        BookStudyProgress bookStudyProgress = (BookStudyProgress) studyProgress.studyProgress();

        return BookStudyProgressResponse.builder()
            .idx(bookStudyProgress.round().idx())
            .startDate(bookStudyProgress.round().startDate())
            .endDate(bookStudyProgress.round().endDate())
            .assignments(Map.of(
                1L, new AssignmentInfo(1L, "개략적인 규모 추정", "챕터 2", 15, 20),
                2L, new AssignmentInfo(2L, "키, 값 저장소 설계", "챕터 6", 30, 35),
                3L, new AssignmentInfo(3L, "URL 단축기 설계", "챕터 8", 40, 45)
            ))
            .users(Map.of(
                1L, new MemberAndReportInfo("msjang4", 1L, null, null),
                2L, new MemberAndReportInfo("platinouss", 2L, null,
                    new QuizInfo("3dsfjiw")),
                3L, new MemberAndReportInfo("sseunghoon99", 3L,
                    new VideoInfo("vvv"), null),
                4L, new MemberAndReportInfo("mentos", 2L,
                    new VideoInfo("555"),
                    new QuizInfo("555")
                )))
            .build();
    }

}
