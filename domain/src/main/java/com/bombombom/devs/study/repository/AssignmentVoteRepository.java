package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.AssignmentVote;
import com.bombombom.devs.study.model.Round;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssignmentVoteRepository extends JpaRepository<AssignmentVote, Long> {


    @Query("SELECT av FROM AssignmentVote av WHERE round = :round AND av.user.id = :userId ")
    Optional<AssignmentVote> findByRoundAndUserId(Round round, Long userId);

    @Query("SELECT av FROM AssignmentVote av WHERE round = :round")
    List<AssignmentVote> findAllByRound(Round round);
}
