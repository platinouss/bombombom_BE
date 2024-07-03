package com.bombombom.devs.algo.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AlgorithmProblemTest {

    @Test
    @DisplayName("AlgorithmProblem 객체를 생성할 수 있다.")
    void can_create_algorithm_problem() {
        /*
         * Given
         */
        Integer refId = 1;
        AlgoTag tag = AlgoTag.DP;
        String title = "Test Problem";
        String link = "https://test.com";
        Integer difficulty = 3;

        /*
         * When
         */
        AlgorithmProblem problem = AlgorithmProblem.builder()
            .refId(refId)
            .tag(tag)
            .title(title)
            .link(link)
            .difficulty(difficulty)
            .build();

        /*
         * Then
         */
        assertThat(problem).isNotNull();
        assertThat(problem.getRefId()).isEqualTo(refId);
        assertThat(problem.getTag()).isEqualTo(tag);
        assertThat(problem.getTitle()).isEqualTo(title);
        assertThat(problem.getLink()).isEqualTo(link);
        assertThat(problem.getDifficulty()).isEqualTo(difficulty);
    }
}
