package com.bombombom.devs.study.model;

import com.bombombom.devs.algo.models.AlgoTag;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.util.Pair;

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

    public Map<String, Pair<Integer, Integer>> getDifficultySpreadForEachTag() {
        return Map.of(
            AlgoTag.MATH.name(), getDifficultySpread(difficultyMath),
            AlgoTag.DP.name(), getDifficultySpread(difficultyDp),
            AlgoTag.GREEDY.name(), getDifficultySpread(difficultyGreedy),
            AlgoTag.IMPLEMENTATION.name(), getDifficultySpread(difficultyImpl),
            AlgoTag.GRAPHS.name(), getDifficultySpread(difficultyGraph),
            AlgoTag.GEOMETRY.name(), getDifficultySpread(difficultyGeometry),
            AlgoTag.DATA_STRUCTURES.name(), getDifficultySpread(difficultyDs),
            AlgoTag.STRING.name(), getDifficultySpread(difficultyString)
        );
    }

    private Pair<Integer, Integer> getDifficultySpread(Float difficulty) {
        Integer spreadLeft = Math.round(difficulty);
        Integer spreadRight = spreadLeft + difficultyGap;
        return Pair.of(spreadLeft, spreadRight);
    }
}
