package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.AlgorithmProblemSolvedHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlgorithmProblemSolvedHistoryRepository extends
    JpaRepository<AlgorithmProblemSolvedHistory, Long> {

    @Query("SELECT h FROM AlgorithmProblemSolvedHistory h "
        + "WHERE h.user.id IN :membersId AND h.problem.id IN :algorithmProblemIds"
        + " AND h.solvedAt IS NOT NULL")
    List<AlgorithmProblemSolvedHistory> findSolvedHistoryWithUserAndProblem(
        List<Long> membersId, List<Long> algorithmProblemIds);

    Optional<AlgorithmProblemSolvedHistory> findByUserIdAndProblemId(Long userId, Long problemId);

    @Query("SELECT h FROM AlgorithmProblemSolvedHistory h "
        + "WHERE h.user.id = :userId AND h.problem.id IN :problemIds")
    List<AlgorithmProblemSolvedHistory> findByUserIdAndProblemIds(Long userId,
        List<Long> problemIds);
}
