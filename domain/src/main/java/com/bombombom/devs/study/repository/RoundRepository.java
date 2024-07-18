package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.Round;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoundRepository extends JpaRepository<Round, Long> {

    @Query("select r from Round r "
        + "join fetch r.study s "
        + "join fetch s.userStudies us "
        + "join fetch us.user "
        + "where r.startDate = :startDate")
    List<Round> findRoundsWithStudyByStartDate(LocalDate startDate);

    @Query("SELECT r.id FROM Round r"
        + " WHERE r.study.id = :studyId AND r.endDate > :currentDate "
        + "ORDER BY r.endDate ASC "
        + "LIMIT 1")
    Optional<Long> findRoundByStudyIdAndEndDate(Long studyId, LocalDate currentDate);
}

