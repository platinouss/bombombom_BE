package com.bombombom.devs.study.model;

import com.bombombom.devs.algo.models.AlgorithmProblem;
import com.bombombom.devs.global.audit.BaseEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Round extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id",
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Study study;

    private Integer idx;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy = "round", cascade = CascadeType.PERSIST)
    private List<AlgorithmProblemAssignment> assignments;

    public void assignProblems(
        List<AlgorithmProblem> unSolvedProblems) {
        List<AlgorithmProblemAssignment> newAssignments = new ArrayList<>();
        for (AlgorithmProblem problem : unSolvedProblems) {
            newAssignments.add(assignProblem(problem));
        }
        assignments = newAssignments;
    }

    public AlgorithmProblemAssignment assignProblem(AlgorithmProblem problem) {
        AlgorithmProblemAssignment assignment = AlgorithmProblemAssignment.builder()
            .round(this)
            .problem(problem)
            .solveHistories(new ArrayList<>())
            .build();
        for (UserStudy userStudy : study.userStudies) {
            AlgorithmProblemAssignmentSolveHistory history
                = assignment.createSolveHistory(userStudy.getUser());
            assignment.getSolveHistories().add(history);
        }
        return assignment;
    }
}
