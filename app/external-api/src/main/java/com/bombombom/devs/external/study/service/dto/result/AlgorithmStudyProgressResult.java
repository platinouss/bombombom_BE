package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.external.study.service.dto.result.progress.AlgorithmStudyProgress;
import com.bombombom.devs.study.enums.StudyType;
import com.bombombom.devs.user.model.User;
import java.util.List;
import lombok.Builder;

@Builder
public record AlgorithmStudyProgressResult(
    StudyType studyType,
    List<User> studyMembers,
    AlgorithmStudyProgress studyProgress
) {

    public static AlgorithmStudyProgressResult fromEntity(
        StudyType studyType,
        List<User> studyMembers,
        AlgorithmStudyProgress studyProgress
    ) {
        return AlgorithmStudyProgressResult.builder()
            .studyType(studyType)
            .studyMembers(studyMembers)
            .studyProgress(studyProgress)
            .build();
    }
}
