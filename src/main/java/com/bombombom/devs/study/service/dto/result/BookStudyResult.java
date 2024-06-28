package com.bombombom.devs.study.service.dto.result;

import com.bombombom.devs.book.service.dto.SearchBooksResult;
import com.bombombom.devs.book.service.dto.SearchBooksResult.BookResult;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.user.service.dto.UserProfileResult;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record BookStudyResult(
    Long id,
    String name,
    String introduce,
    Integer capacity,
    Integer headCount,
    Integer weeks,
    LocalDate startDate,
    Integer reliabilityLimit,
    Integer penalty,
    StudyStatus state,
    UserProfileResult leader,
    StudyType studyType,
    BookResult bookResult
) implements StudyResult {

    public static BookStudyResult fromEntity(BookStudy bookStudy) {

        return BookStudyResult.builder()
            .id(bookStudy.getId())
            .name(bookStudy.getName())
            .introduce(bookStudy.getIntroduce())
            .capacity(bookStudy.getCapacity())
            .headCount(bookStudy.getHeadCount())
            .weeks(bookStudy.getWeeks())
            .startDate(bookStudy.getStartDate())
            .reliabilityLimit(bookStudy.getReliabilityLimit())
            .penalty(bookStudy.getPenalty())
            .state(bookStudy.getState())
            .leader(UserProfileResult.fromEntity(bookStudy.getUser()))
            .bookResult(SearchBooksResult.fromEntity(bookStudy.getBook()))
            .studyType(bookStudy.getStudyType())
            .build();
    }
}