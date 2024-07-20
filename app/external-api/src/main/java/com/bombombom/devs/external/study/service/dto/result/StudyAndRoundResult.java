package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.external.study.service.dto.query.GetStudyProgressQuery;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import lombok.Builder;

@Builder
public record StudyAndRoundResult(
    Study study,
    Round round
) {

    public static StudyAndRoundResult fromEntity(Study study, Round round) {
        return StudyAndRoundResult.builder()
            .study(study)
            .round(round)
            .build();
    }

    public GetStudyProgressQuery toServiceDto() {
        return GetStudyProgressQuery.builder()
            .study(study)
            .round(round)
            .build();
    }

}
