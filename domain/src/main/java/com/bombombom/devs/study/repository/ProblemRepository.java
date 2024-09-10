package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.Assignment;
import com.bombombom.devs.study.model.Problem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProblemRepository extends JpaRepository<Problem, Long> {


    @Query("SELECT p FROM Problem p "
        + "WHERE assignment IN :assignments AND p.examiner.id IN :memberIds")
    List<Problem> findAllByAssignmentInAndExaminerIdIn(
        List<Assignment> assignments, List<Long> memberIds);
}
