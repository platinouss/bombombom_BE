package com.bombombom.devs.study.model;

import static com.bombombom.devs.algo.model.AlgorithmProblemFeedback.FeedbackDifficultyMedian;

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

    private void adjustDifficulty(AlgoTag tag, Float variance) {
        switch (tag) {
            case AlgoTag.DP -> {
                this.difficultyDp += variance;
            }
            case AlgoTag.GEOMETRY -> {
                this.difficultyGeometry += variance;
            }
            case AlgoTag.DATA_STRUCTURES -> {
                this.difficultyDs += variance;
            }
            case AlgoTag.GRAPHS -> {
                this.difficultyGraph += variance;
            }
            case AlgoTag.GREEDY -> {
                this.difficultyGreedy += variance;
            }
            case AlgoTag.IMPLEMENTATION -> {
                this.difficultyImpl += variance;
            }
            case AlgoTag.MATH -> {
                this.difficultyMath += variance;
            }
            case AlgoTag.STRING -> {
                this.difficultyString += variance;
            }
            default -> throw new IllegalStateException("Incorrect use of AlgoTag");
        }
    }

    private Float getDifficultyVariance(AlgorithmProblemFeedback feedback) {
        return (FeedbackDifficultyMedian - feedback.getDifficulty())
            / headCount.floatValue();
    }

    /***
     *
     * @param
     * @return
     *
     * feedback 정보에는 DifficultyMedian을 중앙값으로 가지는 개인의 주관적인 난이도 평가값인 diffculty가 있습니다.
     * 난이도는 스터디 멤버들이 제출한 feedback의 (DifficultyMedian - difficulty)의 평균값만큼 변동됩니다.
     */
    public void applyFeedback(AlgorithmProblemFeedback feedback) {

        adjustDifficulty(feedback.getProblem().getTag(),
            getDifficultyVariance(feedback));
    }

    public void changeFeedback(AlgorithmProblemFeedback preFeedback,
        AlgorithmProblemFeedback newFeedback) {

        adjustDifficulty(preFeedback.getProblem().getTag(),
            getDifficultyVariance(newFeedback) - getDifficultyVariance(preFeedback));
    }

}
