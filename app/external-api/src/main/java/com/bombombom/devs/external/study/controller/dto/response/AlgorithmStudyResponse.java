package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.core.Spread;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.external.study.service.dto.result.AlgorithmStudyResult;
import com.bombombom.devs.external.user.controller.dto.UserProfileResponse;
import com.bombombom.devs.study.enums.StudyStatus;
import com.bombombom.devs.study.enums.StudyType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;

@Builder
public record AlgorithmStudyResponse(
    Long id,
    String name,
    String introduce,
    Integer capacity,
    Integer headCount,
    Integer weeks,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate,
    Integer reliabilityLimit,
    Integer penalty,
    StudyStatus state,
    UserProfileResponse leader,
    StudyType studyType,

    Map<AlgoTag, Spread> difficultySpreadMap,
    Integer difficultyGap,

    Integer problemCount)
    implements StudyResponse {

    public static AlgorithmStudyResponse fromResult(AlgorithmStudyResult res) {

        return builder()
            .id(res.id())
            .name(res.name())
            .introduce(res.introduce())
            .capacity(res.capacity())
            .headCount(res.headCount())
            .weeks(res.weeks())
            .startDate(res.startDate())
            .reliabilityLimit(res.reliabilityLimit())
            .penalty(res.penalty())
            .leader(UserProfileResponse.fromResult(res.leader()))
            .difficultySpreadMap(res.difficultySpreadMap())
            .state(res.state())
            .studyType(res.studyType())
            .difficultyGap(res.difficultyGap())
            .problemCount(res.problemCount())
            .build();

    }
}
