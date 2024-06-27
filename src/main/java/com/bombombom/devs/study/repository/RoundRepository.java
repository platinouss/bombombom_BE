package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.models.Round;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoundRepository extends JpaRepository<Round, Long> {

    @Query("select r from Round r join fetch r.study where r.startDate = :startDate")
    List<Round> findRoundsWithRoundsByStartDate(LocalDate startDate);
}

