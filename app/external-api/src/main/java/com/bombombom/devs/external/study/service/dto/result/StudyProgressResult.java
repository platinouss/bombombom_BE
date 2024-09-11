package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.external.study.service.dto.result.progress.StudyProgress;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.study.enums.StudyType;
import com.bombombom.devs.user.model.User;
import java.util.List;
import lombok.Builder;

@Builder
public record StudyProgressResult(
    StudyType studyType,
    List<UserProfileResult> members,
    StudyProgress studyProgress
) {

    public static StudyProgressResult fromEntity(
        StudyType studyType,
        List<User> members,
        StudyProgress studyProgress
    ) {
        return StudyProgressResult.builder()
            .studyType(studyType)
            .members(members.stream().map(UserProfileResult::fromEntity).toList())
            .studyProgress(studyProgress)
            .build();
    }
}
