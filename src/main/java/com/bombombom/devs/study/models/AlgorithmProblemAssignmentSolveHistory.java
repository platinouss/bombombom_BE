package com.bombombom.devs.study.models;

import com.bombombom.devs.global.audit.BaseEntity;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "algorithm_problem_assignment_solve_history")
public class AlgorithmProblemAssignmentSolveHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id",
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private AlgorithmProblemAssignment assignment;

    @Column(name = "solved_at")
    private LocalDateTime solvedAt;

    @Column(name = "try_count")
    private Integer tryCount;
}
