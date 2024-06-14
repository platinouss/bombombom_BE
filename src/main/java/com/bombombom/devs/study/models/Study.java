package com.bombombom.devs.study.models;

import com.bombombom.devs.global.audit.BaseEntity;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;


@Entity
@Getter
@SuperBuilder
@Table(name = "study")
@DiscriminatorColumn(name = "STUDY_TYPE")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public abstract class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    @Column
    protected String name;

    @NotNull
    @Column(length = 500)
    protected String introduce;

    @Column
    protected Integer capacity;

    @NotNull
    @Column(name = "head_count")
    protected Integer headCount;

    @Column
    protected Integer weeks;

    @Column(name = "start_date")
    protected LocalDate startDate;

    @Column(name = "reliability_limit")
    protected Integer reliabilityLimit;

    @Column
    protected Integer penalty;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected StudyStatus state;

    abstract public StudyType getStudyType();


    @PrePersist
    private void onCreate() {
        state = StudyStatus.READY;
        headCount = 1;
    }
}
