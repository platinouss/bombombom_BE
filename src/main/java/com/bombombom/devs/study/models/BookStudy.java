package com.bombombom.devs.study.models;

import com.bombombom.devs.book.models.Book;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@SuperBuilder
@Table(name = "book_study")
@DiscriminatorValue(StudyType.Values.BOOK)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookStudy extends Study {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn",
        referencedColumnName = "isbn",
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Book book;

    @Override
    public StudyType getStudyType() {
        return StudyType.BOOK;
    }
}
