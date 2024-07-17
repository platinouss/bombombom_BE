package com.bombombom.devs.algo.model;

import com.bombombom.devs.common.BaseEntity;
import com.bombombom.devs.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "algorithm_problem_feedback")
public class AlgorithmProblemFeedback extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "algorithm_problem_feedback_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id",
        nullable = false,
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private AlgorithmProblem problem;

    @Column
    private Boolean again;

    @Column
    private Integer difficulty;

    public static final int FeedbackDifficultyBegin = 1;
    public static final int FeedbackDifficultyEnd = 5;
    public static final float FeedbackDifficultyMedian =
        (FeedbackDifficultyBegin + FeedbackDifficultyEnd) / 2f;


    public void update(AlgorithmProblemFeedback newFeedback) {
        again = newFeedback.getAgain();
        difficulty = newFeedback.getDifficulty();
    }
}
