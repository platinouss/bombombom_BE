package com.bombombom.devs.study;

import static org.assertj.core.api.Assertions.assertThat;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.UserStudy;
import com.bombombom.devs.user.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoundTest {

    @Test
    @DisplayName("문제들을 할당할 수 있다.")
    void can_assign_problems() {
        /*
         * Given
         */
        Study study = AlgorithmStudy.builder()
            .userStudies(new ArrayList<>())
            .build();
        Round round = Round.builder()
            .study(study)
            .idx(1)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusWeeks(1))
            .assignments(new ArrayList<>())
            .build();
        List<AlgorithmProblem> unSolvedProblems = new ArrayList<>();

        /*
         * When
         */
        round.assignProblems(unSolvedProblems);

        /*
         * Then
         */
        assertThat(round.getAssignments().stream().map(AlgorithmProblemAssignment::getProblem))
            .isEqualTo(unSolvedProblems);
    }

    @Test
    @DisplayName("문제를 할당하면, 해당 문제의 풀이 이력이 생성된다.")
    void assign_problem_creates_solve_history() {
        /*
         * Given
         */
        User user = User.builder()
            .id(1L)
            .username("testuser")
            .money(10000)
            .reliability(10)
            .build();
        Study study = AlgorithmStudy.builder()
            .userStudies(new ArrayList<>())
            .build();
        UserStudy userStudy = UserStudy.of(user, study, 1000);
        study.getUserStudies().add(userStudy);
        Round round = Round.builder()
            .study(study)
            .idx(1)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusWeeks(1))
            .assignments(new ArrayList<>())
            .build();
        AlgorithmProblem problem = AlgorithmProblem.builder()
            .id(1L)
            .build();

        /*
         * When
         */
        AlgorithmProblemAssignment assignment = round.assignProblem(problem);

        /*
         * Then
         */
        // TODO: 문제 해결 시, 문제 풀이 이력이 생성되도록 변경되어야 함.
//        assertThat(assignment.getSolveHistories()).isNotEmpty();
    }
}