package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.AlgorithmProblemAssignmentSolveHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlgorithmProblemAssignmentSolveHistoryRepository extends
    JpaRepository<AlgorithmProblemAssignmentSolveHistory, Long> {

    @Query("SELECT h FROM AlgorithmProblemAssignmentSolveHistory h "
        + "WHERE h.user.id IN :studyMemberIds AND h.problem.id IN :algorithmProblemIds")
    List<AlgorithmProblemAssignmentSolveHistory> findWithUserAndProblem(List<Long> studyMemberIds,
        List<Long> algorithmProblemIds);
}
