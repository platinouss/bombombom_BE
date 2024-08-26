package com.bombombom.devs.algo.repository;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlgorithmProblemRepository extends JpaRepository<AlgorithmProblem, Long> {

    Optional<AlgorithmProblem> findByRefId(Integer refId);

}
