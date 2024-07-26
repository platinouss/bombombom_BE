package com.bombombom.devs.algo.model;

import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Table(name = "algorithm_problem")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlgorithmProblem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_id")
    private Integer refId;

    private AlgoTag tag;

    private String title;

    private String link;

    private Integer difficulty;

    @OneToMany(mappedBy = "problem", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    List<AlgorithmProblemFeedback> feedbacks = new ArrayList<>();

    public void addFeedback(AlgorithmProblemFeedback algorithmProblemFeedback) {

        feedbacks.add(algorithmProblemFeedback);
    }


}
