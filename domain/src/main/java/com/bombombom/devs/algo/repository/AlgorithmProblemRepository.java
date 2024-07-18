package com.bombombom.devs.algo.repository;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlgorithmProblemRepository extends JpaRepository<AlgorithmProblem, Long> {

    @Query("SELECT p FROM AlgorithmProblem p "
        + "WHERE p.refId IN :problemIds")
    List<AlgorithmProblem> findAlgorithmProblemsById(List<Long> problemIds);
}
