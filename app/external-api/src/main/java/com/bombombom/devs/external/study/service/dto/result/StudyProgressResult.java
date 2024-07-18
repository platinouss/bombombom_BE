package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.user.model.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StudyProgressResult<T> {

    StudyType studyType;
    List<User> studyMembers;
    T studyProgress;

    public static <T> StudyProgressResult<T> fromEntity(
        StudyType studyType,
        List<User> studyMembers,
        T studyProgress
    ) {
        return StudyProgressResult.<T>builder()
            .studyType(studyType)
            .studyMembers(studyMembers)
            .studyProgress(studyProgress)
            .build();

    }
}
