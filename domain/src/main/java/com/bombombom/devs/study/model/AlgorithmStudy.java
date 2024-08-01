package com.bombombom.devs.study.model;

import static com.bombombom.devs.algo.model.AlgorithmProblemFeedback.FeedbackDifficultyAverage;

import com.bombombom.devs.algo.model.AlgorithmProblemFeedback;
import com.bombombom.devs.core.Spread;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.util.Util;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
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

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    @Builder.Default
    protected List<AlgorithmStudyDifficulty> difficulties = new ArrayList<>();

    @Column(name = "difficulty_gap")
    private Integer difficultyGap;

    @Column(name = "problem_count")
    private Integer problemCount;


    @Override
    public StudyType getStudyType() {
        return StudyType.ALGORITHM;
    }

    public void setDifficulty(Float difficulty) {
        AlgoTag.getTagNames().forEach(
            tag -> {
                difficulties.add(AlgorithmStudyDifficulty.builder()
                    .study(this)
                    .algoTag(AlgoTag.valueOf(tag))
                    .difficulty(difficulty)
                    .build());
            }
        );
    }

    public Map<AlgoTag, Spread> getDifficultySpreadMap() {
        return difficulties.stream().collect(
            Collectors.toMap(AlgorithmStudyDifficulty::getAlgoTag,
                algorithmStudyDifficulty ->
                    getDifficultySpread(algorithmStudyDifficulty.getDifficulty())
            )
        );
    }

    private Spread getDifficultySpread(Float difficulty) {

        Integer spreadLeft = Util.ensureRange(Math.round(difficulty), MIN_DIFFICULTY_LEVEL,
            MAX_DIFFICULTY_LEVEL);

        Integer spreadRight = Util.ensureRange(spreadLeft + difficultyGap, MIN_DIFFICULTY_LEVEL,
            MAX_DIFFICULTY_LEVEL);
        return Spread.of(spreadLeft, spreadRight);
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
