package com.bombombom.devs.study.models;

import com.bombombom.devs.algo.models.AlgorithmProblem;
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
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
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

    @OneToMany(mappedBy = "round")
    private List<AlgorithmProblemAssignment> assignments;

    public List<AlgorithmProblemAssignment> assignProblems(
        List<AlgorithmProblem> unSolvedProblems) {
        for (AlgorithmProblem problem : unSolvedProblems) {
            assignProblem(problem);
        }
        return assignments;
    }

    public AlgorithmProblemAssignment assignProblem(AlgorithmProblem problem) {
        /*
        TODO: solve_history 같이 저장되는지 확인
         만약 같이 저장 안되면 CascadeType 변경하면 저장되는지 확인
         */
        AlgorithmProblemAssignment assignment = AlgorithmProblemAssignment.builder()
            .round(this)
            .problem(problem)
            .build();
        for (UserStudy userStudy: study.userStudies) {
            AlgorithmProblemAssignmentSolveHistory history
                = assignment.createSolveHistory(userStudy.getUser());
            assignment.getSolveHistories().add(history);
        }

        assignments.add(assignment);
        return assignment;
    }
}
