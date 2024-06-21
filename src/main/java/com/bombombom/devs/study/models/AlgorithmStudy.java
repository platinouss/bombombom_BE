package com.bombombom.devs.study.models;

import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@SuperBuilder
@Table(name = "algorithm_study")
@DiscriminatorValue(StudyType.Values.ALGORITHM)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlgorithmStudy extends Study {

    @Column(name = "difficulty_math")
    private Float difficultyMath;

    @Column(name = "difficulty_dp")
    private Float difficultyDp;

    @Column(name = "difficulty_greedy")
    private Float difficultyGreedy;

    @Column(name = "difficulty_impl")
    private Float difficultyImpl;

    @Column(name = "difficulty_graph")
    private Float difficultyGraph;

    @Column(name = "difficulty_geometry")
    private Float difficultyGeometry;

    @Column(name = "difficulty_ds")
    private Float difficultyDs;

    @Column(name = "difficulty_string")
    private Float difficultyString;

    @Column(name = "difficulty_gap")
    private Integer difficultyGap;

    @Column(name = "problem_count")
    private Integer problemCount;

    @Override
    public StudyType getStudyType() {

        return StudyType.ALGORITHM;
    }
}
