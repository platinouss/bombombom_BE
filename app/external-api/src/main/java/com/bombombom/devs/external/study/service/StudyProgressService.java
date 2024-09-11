package com.bombombom.devs.external.study.service;

import com.bombombom.devs.external.study.service.dto.result.progress.StudyProgress;
import com.bombombom.devs.study.enums.StudyType;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.user.model.User;
import java.util.List;

// TODO: 추후 명확한 이름 변경 필요
public interface StudyProgressService {

    StudyType getStudyType();

    StudyProgress findStudyProgress(Round round, List<User> members);

    void startRound(Study study, Round round);
}
