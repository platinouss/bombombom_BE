package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.user.model.User;
import java.util.List;

public interface StudyProgressResult<T> {

    StudyType studyType();

    List<User> studyMembers();

    T studyProgress();
}
