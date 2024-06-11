package com.bombombom.devs.study.models;

import com.bombombom.devs.study.service.dto.result.AlgorithmStudyResult;
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
@Table(name = "algorithm_study")
@DiscriminatorValue(StudyType.Values.ALGORITHM)
@DynamicInsert
public class AlgorithmStudy extends Study {

    @Column(name="difficulty_math")
    private float difficultyMath;

    @Column(name="difficulty_dp")
    private float difficultyDp;

    @Column(name="difficulty_greedy")
    private float difficultyGreedy;

    @Column(name="difficulty_impl")
    private float difficultyImpl;

    @Column(name="difficulty_graph")
    private float difficultyGraph;

    @Column(name="difficulty_geometry")
    private float difficultyGeometry;

    @Column(name="difficulty_ds")
    private float difficultyDs;

    @Column(name="difficulty_string")
    private float difficultyString;

    @Column(name="difficulty_gap")
    private int difficultyGap;

    @Column(name="problem_count")
    private int problemCount;

    public AlgorithmStudyResult toDto() {
        return AlgorithmStudyResult.builder()
            .name(name).introduce(introduce).capacity(capacity)
            .headCount(headCount).weeks(weeks).startDate(startDate)
            .reliabilityLimit(reliabilityLimit).penalty(penalty).state(state)
            .difficultyDs(difficultyDs).difficultyGraph(difficultyGraph)
            .difficultyString(difficultyString).difficultyGeometry(difficultyGeometry)
            .difficultyMath(difficultyMath).difficultyImpl(difficultyImpl)
            .difficultyGap(difficultyGap).difficultyGreedy(difficultyGreedy)
            .difficultyDp(difficultyDp).studyType(getType()).build();
    }

    @Override
    public StudyType getType() {

        return StudyType.ALGORITHM;
    }
}
