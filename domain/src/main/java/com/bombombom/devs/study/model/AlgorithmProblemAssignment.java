package com.bombombom.devs.study.model;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.common.BaseEntity;
import com.bombombom.devs.user.model.User;
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

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "algorithm_problem_assignment")
public class AlgorithmProblemAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id",
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id",
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private AlgorithmProblem problem;

    public AlgorithmProblemAssignmentSolveHistory createSolveHistory(User user) {
        return AlgorithmProblemAssignmentSolveHistory.builder()
            .problem(problem)
            .user(user)
            .tryCount(0)
            .build();
    }
}
