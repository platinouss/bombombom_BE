package com.bombombom.devs.external.study.service.dto.query;

import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.user.model.User;
import java.util.List;
import lombok.Builder;

@Builder
public record GetStudyProgressQuery(
    Study study,
    Round round
) {

    public static GetStudyProgressQuery fromEntity(Study study, Round round) {
        return GetStudyProgressQuery.builder()
            .study(study)
            .round(round)
            .build();
    }

    public GetAlgorithmStudyProgressQuery toAlgorithmProgressQuery(List<User> studyMembers) {
        return GetAlgorithmStudyProgressQuery.builder()
            .round(round)
            .studyMembersId(studyMembers.stream().map(User::getId).toList())
            .build();
    }

}
