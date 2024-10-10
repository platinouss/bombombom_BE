package com.bombombom.devs.algo.repository;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlgorithmProblemRepository extends JpaRepository<AlgorithmProblem, Long> {

    @Query("SELECT p FROM AlgorithmProblem p WHERE p.id IN :problemIds")
    List<AlgorithmProblem> findAllById(List<Long> problemIds);

    @Query("SELECT p FROM AlgorithmProblem p WHERE p.refId IN :problemRefIds")
    List<AlgorithmProblem> findAllByRefId(List<Integer> problemRefIds);

    Optional<AlgorithmProblem> findByRefId(Integer refId);
}
