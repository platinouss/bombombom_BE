package com.bombombom.devs.external.study.service.dto.result.progress;

import com.bombombom.devs.external.study.service.dto.result.RoundResult;
import com.bombombom.devs.study.model.Round;
import lombok.Builder;

// TODO: 서적 스터디 진행 현황 조회 시 필요한 필드 추가
@Builder
public record BookStudyProgress(
    RoundResult round
) implements StudyProgress {

    public static BookStudyProgress fromEntity(
        Round round
    ) {
        return BookStudyProgress.builder()
            .round(RoundResult.fromEntity(round))
            .build();
    }

}
