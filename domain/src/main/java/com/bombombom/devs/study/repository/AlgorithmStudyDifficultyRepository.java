package com.bombombom.devs.study.repository;

import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.study.model.AlgorithmStudyDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AlgorithmStudyDifficultyRepository extends
    JpaRepository<AlgorithmStudyDifficulty, Long> {

    @Modifying
    @Query(value = "UPDATE AlgorithmStudyDifficulty asd "
        + "SET difficulty = difficulty+:variance "
        + "WHERE asd.algoTag = :algoTag AND asd.study.id = :studyId")
    int increaseDifficultyByStudyIdAndAlgoTag(Long studyId, AlgoTag algoTag, Float variance);

}
