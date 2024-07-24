package com.bombombom.devs.external.study.service.dto.result.progress;

import com.bombombom.devs.study.model.Round;

// TODO: 서적 스터디 진행 현황 조회 시 필요한 필드 추가
public record BookStudyProgress(
    Round round
) implements StudyProgress {

}
