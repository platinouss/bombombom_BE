package com.bombombom.devs.external.study.service.dto.query;

import com.bombombom.devs.study.model.Round;
import java.util.List;
import lombok.Builder;

@Builder
public record GetAlgorithmStudyProgressQuery(
    Round round,
    List<Long> studyMembersId
) {

}
