package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.external.book.service.dto.SearchBooksResult;
import com.bombombom.devs.external.book.service.dto.SearchBooksResult.BookResult;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.study.enums.StudyStatus;
import com.bombombom.devs.study.enums.StudyType;
import com.bombombom.devs.study.enums.VotingProcess;
import com.bombombom.devs.study.model.BookStudy;
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
    BookResult bookResult,
    VotingProcess votingProcess,
    Boolean duplicated
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
            .leader(UserProfileResult.fromEntity(bookStudy.getLeader()))
            .bookResult(SearchBooksResult.fromBook(bookStudy.getBook()))
            .studyType(bookStudy.getStudyType())
            .votingProcess(bookStudy.getVotingProcess())
            .duplicated(bookStudy.isDuplicated())
            .build();
    }
}