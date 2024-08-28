package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.core.Spread;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.StudyStatus;
import com.bombombom.devs.study.model.StudyType;
import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;

@Builder
public record AlgorithmStudyResult(
    Long id,
    String name,
    String introduce,
    Integer capacity,
    Integer headCount,
    Integer weeks,
    UserProfileResult leader,
    LocalDate startDate,
    Integer reliabilityLimit,
    Integer penalty,
    StudyStatus state,
    StudyType studyType,
    Integer difficultyGap,
    Map<AlgoTag, Spread> difficultySpreadMap,
    Integer problemCount) implements StudyResult {

    public static AlgorithmStudyResult fromEntity(AlgorithmStudy algorithmStudy) {

        return AlgorithmStudyResult.builder()
            .id(algorithmStudy.getId())
            .name(algorithmStudy.getName())
            .introduce(algorithmStudy.getIntroduce())
            .capacity(algorithmStudy.getCapacity())
            .headCount(algorithmStudy.getHeadCount())
            .weeks(algorithmStudy.getWeeks())
            .startDate(algorithmStudy.getStartDate())
            .reliabilityLimit(algorithmStudy.getReliabilityLimit())
            .penalty(algorithmStudy.getPenalty())
            .leader(UserProfileResult.fromEntity(algorithmStudy.getLeader()))
            .difficultySpreadMap(algorithmStudy.getDifficultySpreadMap())
            .state(algorithmStudy.getState())
            .difficultyGap(algorithmStudy.getDifficultyGap())
            .problemCount(algorithmStudy.getProblemCount())
            .studyType(algorithmStudy.getStudyType())
            .build();
    }
}
