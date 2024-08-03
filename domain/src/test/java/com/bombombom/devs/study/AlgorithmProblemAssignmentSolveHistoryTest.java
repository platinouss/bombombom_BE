package com.bombombom.devs.study;

import static org.assertj.core.api.Assertions.assertThat;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.study.model.AlgorithmProblemSolvedHistory;
import com.bombombom.devs.user.model.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AlgorithmProblemAssignmentSolveHistoryTest {

    @Test
    @DisplayName("AlgorithmProblemAssignmentSolveHistory 객체를 생성할 수 있다.")
    void can_create_algorithm_problem_assignment_solve_history() {
        /*
         * Given
         */
        AlgorithmProblem problem = AlgorithmProblem.builder()
            .refId(1)
            .build();
        User user = User.builder()
            .id(1L)
            .username("testuser")
            .build();

        /*
         * When
         */
        AlgorithmProblemSolvedHistory history = AlgorithmProblemSolvedHistory.builder()
            .user(user)
            .problem(problem)
            .solvedAt(LocalDateTime.now())
            .tryCount(1)
            .build();

        /*
         * Then
         */
        assertThat(history).isNotNull();
        assertThat(history.getProblem()).isEqualTo(problem);
        assertThat(history.getUser()).isEqualTo(user);
        assertThat(history.getSolvedAt()).isNotNull();
        assertThat(history.getTryCount()).isEqualTo(1);
    }
}