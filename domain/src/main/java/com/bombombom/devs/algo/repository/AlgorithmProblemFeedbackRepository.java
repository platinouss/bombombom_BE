package com.bombombom.devs.algo.repository;

import com.bombombom.devs.algo.model.AlgorithmProblemFeedback;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlgorithmProblemFeedbackRepository extends
    JpaRepository<AlgorithmProblemFeedback, Long> {

    Optional<AlgorithmProblemFeedback> findByUserIdAndProblemId(Long userId, Long problemId);

}
