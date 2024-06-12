package com.bombombom.devs.study.models;

import com.bombombom.devs.global.audit.BaseEntity;
import com.bombombom.devs.study.models.StudyStatus.Values;
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
import jakarta.persistence.Table;
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

    @Column(nullable = false)
    protected String name;

    @Column(length = 500, nullable = false)
    protected String introduce;

    @Column
    protected int capacity;

    @ColumnDefault("1")
    @Column(name = "head_count",nullable = false)
    protected int headCount;

    @Column
    protected int weeks;

    @Column(name="start_date")
    protected LocalDate startDate;

    @Column(name = "reliability_limit")
    protected int reliabilityLimit;

    @Column
    protected int penalty;

    @ColumnDefault("'"+StudyStatus.Values.READY+"'")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected StudyStatus state;

    abstract public StudyType getStudyType();

    abstract public StudyResult toDto();
}
