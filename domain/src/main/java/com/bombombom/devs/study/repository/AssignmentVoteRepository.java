package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.AssignmentVote;
import com.bombombom.devs.study.model.Round;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssignmentVoteRepository extends JpaRepository<AssignmentVote, Long> {


    @Query("SELECT av FROM AssignmentVote av WHERE av.user.id = :userId AND round = :round")
    Optional<AssignmentVote> findByUserIdAndRound(Long userId, Round round);

    @Query("SELECT av FROM AssignmentVote av WHERE round = :round")
    List<AssignmentVote> findAllByRound(Round round);
}
