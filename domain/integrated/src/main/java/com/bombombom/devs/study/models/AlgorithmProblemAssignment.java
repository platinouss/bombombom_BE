package com.bombombom.devs.study.models;

import com.bombombom.devs.algo.models.AlgorithmProblem;
import com.bombombom.devs.global.audit.BaseEntity;
import com.bombombom.devs.user.models.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
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

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.PERSIST)
    private List<AlgorithmProblemAssignmentSolveHistory> solveHistories;

    public AlgorithmProblemAssignmentSolveHistory createSolveHistory(User user) {
        return AlgorithmProblemAssignmentSolveHistory.builder()
            .assignment(this)
            .user(user)
            .tryCount(0)
            .build();
    }
}
