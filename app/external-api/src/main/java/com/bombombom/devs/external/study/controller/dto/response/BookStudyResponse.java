package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.book.controller.dto.BookInfo;
import com.bombombom.devs.external.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.external.user.controller.dto.UserProfileResponse;
import com.bombombom.devs.study.enums.StudyStatus;
import com.bombombom.devs.study.enums.StudyType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record BookStudyResponse(
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
    BookInfo bookInfo)
    implements StudyResponse {

    public static BookStudyResponse fromResult(BookStudyResult res) {

        return builder()
            .id(res.id())
            .name(res.name())
            .introduce(res.introduce())
            .capacity(res.capacity())
            .headCount(res.headCount())
            .weeks(res.weeks())
            .leader(UserProfileResponse.fromResult(res.leader()))
            .startDate(res.startDate())
            .reliabilityLimit(res.reliabilityLimit())
            .penalty(res.penalty())
            .state(res.state())
            .studyType(res.studyType())
            .bookInfo(BookInfo.fromResult(res.bookResult()))
            .build();

    }

}
