package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.AlgorithmProblemSolveHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlgorithmProblemSolveHistoryRepository extends
    JpaRepository<AlgorithmProblemSolveHistory, Long> {

    @Query("SELECT h FROM AlgorithmProblemSolveHistory h "
        + "WHERE h.user.id IN :membersId AND h.problem.id IN :algorithmProblemIds"
        + " AND h.solvedAt IS NOT NULL")
    List<AlgorithmProblemSolveHistory> findSolvedHistoryWithUserAndProblem(
        List<Long> membersId, List<Long> algorithmProblemIds);


    Optional<AlgorithmProblemSolveHistory> findByUserIdAndProblemId(Long userId, Long problemId);

}
