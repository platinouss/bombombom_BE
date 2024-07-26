package com.bombombom.devs.study.model;

import static com.bombombom.devs.algo.model.AlgorithmProblemFeedback.FeedbackDifficultyAverage;

import com.bombombom.devs.algo.model.AlgorithmProblemFeedback;
import com.bombombom.devs.core.Pair;
import com.bombombom.devs.core.enums.AlgoTag;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    public Map<AlgoTag, Pair<Integer, Integer>> getDifficultySpreadForEachTag() {
        return Map.of(
            AlgoTag.MATH, getDifficultySpread(difficultyMath),
            AlgoTag.DP, getDifficultySpread(difficultyDp),
            AlgoTag.GREEDY, getDifficultySpread(difficultyGreedy),
            AlgoTag.IMPLEMENTATION, getDifficultySpread(difficultyImpl),
            AlgoTag.GRAPHS, getDifficultySpread(difficultyGraph),
            AlgoTag.GEOMETRY, getDifficultySpread(difficultyGeometry),
            AlgoTag.DATA_STRUCTURES, getDifficultySpread(difficultyDs),
            AlgoTag.STRING, getDifficultySpread(difficultyString)
        );
    }

    private Pair<Integer, Integer> getDifficultySpread(Float difficulty) {
        Integer spreadLeft = Math.round(difficulty);
        Integer spreadRight = spreadLeft + difficultyGap;
        return Pair.of(spreadLeft, spreadRight);
    }


    /**
     * feedback 정보로 줄 수 있는 Difficulty의 평균값을 DifficultyAverage라고 할 때 난이도는 스터디 멤버들이 제출한 feedback의
     * (DifficultyAverage - difficulty)의 평균값만큼 변동됩니다.
     *
     * @param feedback
     * @return Float
     */
    public Float getDifficultyVariance(AlgorithmProblemFeedback feedback) {
        return (FeedbackDifficultyAverage - feedback.getDifficulty())
            / headCount.floatValue();
    }


}
