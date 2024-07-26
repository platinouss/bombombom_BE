package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlgorithmProblemAssignmentRepository
    extends JpaRepository<AlgorithmProblemAssignment, Long> {

    boolean existsByRoundIdAndProblemId(Long roundId,
        Long problemId);

    @Query("SELECT a FROM AlgorithmProblemAssignment a JOIN FETCH a.problem "
        + "WHERE a.round.id = :roundId")
    List<AlgorithmProblemAssignment> findProblemWithStudyByRound(Long roundId);
}
