package com.bombombom.devs.study.models;

import com.bombombom.devs.algo.models.AlgorithmProblem;
import com.bombombom.devs.global.audit.BaseEntity;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "algorithm_problem_assignment")
public class AlgorithmProblemAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id",
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id",
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private AlgorithmProblem problem;
}
