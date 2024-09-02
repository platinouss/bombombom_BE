package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.Assignment;
import com.bombombom.devs.study.model.Round;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AssignmentRepository extends
    JpaRepository<Assignment, Long> {

    @Modifying
    @Query("DELETE from Assignment a WHERE id NOT IN :ids AND round = :round")
    void deleteAllByIdNotInAndRound(Iterable<Long> ids, Round round);
}
