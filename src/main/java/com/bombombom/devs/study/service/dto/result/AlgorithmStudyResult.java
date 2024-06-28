package com.bombombom.devs.study.service.dto.result;

import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.user.service.dto.UserProfileResult;
import java.time.LocalDate;
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
    Float difficultyMath,
    Float difficultyGreedy,
    Float difficultyImpl,
    Float difficultyGraph,
    Float difficultyGeometry,
    Float difficultyDs,
    Float difficultyString,
    Float difficultyDp,
    Integer difficultyGap,
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
            .leader(UserProfileResult.fromEntity(algorithmStudy.getUser()))
            .state(algorithmStudy.getState())
            .difficultyDs(algorithmStudy.getDifficultyDs())
            .difficultyGraph(algorithmStudy.getDifficultyGraph())
            .difficultyString(algorithmStudy.getDifficultyString())
            .difficultyGeometry(algorithmStudy.getDifficultyGeometry())
            .difficultyMath(algorithmStudy.getDifficultyMath())
            .difficultyImpl(algorithmStudy.getDifficultyImpl())
            .difficultyGap(algorithmStudy.getDifficultyGap())
            .difficultyGreedy(algorithmStudy.getDifficultyGreedy())
            .problemCount(algorithmStudy.getProblemCount())
            .difficultyDp(algorithmStudy.getDifficultyDp())
            .studyType(algorithmStudy.getStudyType())
            .build();
    }
}
