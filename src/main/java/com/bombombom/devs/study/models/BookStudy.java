package com.bombombom.devs.study.models;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;


@Entity
@Getter
@SuperBuilder
@Table(name = "book_study")
@DiscriminatorValue(StudyType.Values.BOOK)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookStudy extends Study {

    @NotNull
    @Column(name = "book_id")
    private Long bookId;

    @Override
    public StudyType getStudyType() {
        return StudyType.BOOK;
    }
}
