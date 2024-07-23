package com.bombombom.devs.external.study.service.factory;

import com.bombombom.devs.external.study.service.StudyProgressService;
import com.bombombom.devs.study.model.StudyType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class StudyServiceFactory {

    private final Map<StudyType, StudyProgressService> studyProgressServiceMap = new HashMap<>();

    public StudyServiceFactory(List<StudyProgressService> studyProgressServices) {
        studyProgressServices.forEach(
            service -> studyProgressServiceMap.put(service.getStudyType(), service));
    }

    public StudyProgressService getService(StudyType studyType) {
        return studyProgressServiceMap.get(studyType);
    }

}
