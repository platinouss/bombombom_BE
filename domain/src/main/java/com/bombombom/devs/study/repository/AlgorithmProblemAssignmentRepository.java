package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlgorithmProblemAssignmentRepository
    extends JpaRepository<AlgorithmProblemAssignment, Long> {

    @Query("SELECT a FROM AlgorithmProblemAssignment a JOIN FETCH a.problem "
        + "WHERE a.round.id = ("
        + " SELECT r.id FROM Round r"
        + " WHERE r.study.id = :studyId AND r.endDate > :currentDate "
        + " ORDER BY r.endDate ASC "
        + " LIMIT 1 "
        + ")")
    List<AlgorithmProblemAssignment> findProblemWithStudyByRound(Long studyId,
        LocalDate currentDate);
}
