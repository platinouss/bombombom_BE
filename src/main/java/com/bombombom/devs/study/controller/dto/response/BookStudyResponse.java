package com.bombombom.devs.study.controller.dto.response;

import com.bombombom.devs.book.models.Book;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.user.models.User;
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
    User leader,
    StudyType studyType,
    Book book)
    implements StudyResponse {

    public static BookStudyResponse fromResult(BookStudyResult res) {

        return builder()
            .id(res.id())
            .name(res.name())
            .introduce(res.introduce())
            .capacity(res.capacity())
            .headCount(res.headCount())
            .weeks(res.weeks())
            .leader(res.leader())
            .startDate(res.startDate())
            .reliabilityLimit(res.reliabilityLimit())
            .penalty(res.penalty())
            .state(res.state())
            .studyType(res.studyType())
            .book(res.book())
            .build();

    }

}
