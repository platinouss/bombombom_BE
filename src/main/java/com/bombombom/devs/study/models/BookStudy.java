package com.bombombom.devs.study.models;

import com.bombombom.devs.study.service.dto.result.BookStudyResult;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;


@Entity
@Getter
@SuperBuilder
@Table(name = "book_study")
@DiscriminatorValue(StudyType.Values.BOOK)
@DynamicInsert
public class BookStudy extends Study {

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    public BookStudyResult toDto() {
        return BookStudyResult.builder()
            .id(id).name(name).introduce(introduce).capacity(capacity)
            .headCount(headCount).weeks(weeks).startDate(startDate)
            .reliabilityLimit(reliabilityLimit).penalty(penalty).state(state)
            .bookId(bookId).studyType(getType()).build();
    }

    @Override
    public StudyType getType() {
        return StudyType.BOOK;
    }
}
