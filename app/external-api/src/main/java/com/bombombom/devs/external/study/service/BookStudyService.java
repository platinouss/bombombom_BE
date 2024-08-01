package com.bombombom.devs.external.study.service;

import com.bombombom.devs.external.study.service.dto.result.progress.BookStudyProgress;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.user.model.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookStudyService implements StudyProgressService {

    @Override
    public StudyType getStudyType() {
        return StudyType.BOOK;
    }

    @Override
    public BookStudyProgress findStudyProgress(Round round, List<User> members) {
        // TODO: 서적 스터디 진행 현황 조회 로직 추가
        return null;
    }

    @Override
    public void startRound(Study study, Round round) {
        
    }
}
