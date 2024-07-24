package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.study.service.dto.result.StudyProgressResult;
import lombok.Builder;

// TODO: 서적 스터디 진행 현황 응답 필드 구성 및 fromResult 메서드 수정
@Builder
public record BookStudyProgressResponse() implements StudyProgressResponse {

    public static BookStudyProgressResponse fromResult(StudyProgressResult studyProgress) {
        return BookStudyProgressResponse.builder().build();
    }

}
