package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.Round;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoundRepository extends JpaRepository<Round, Long> {

    @Query("SELECT r FROM Round r "
        + "JOIN FETCH r.study s "
        + "JOIN FETCH s.userStudies us "
        + "JOIN FETCH us.user "
        + "WHERE r.startDate = :startDate")
    List<Round> findRoundsWithStudyByStartDate(LocalDate startDate);

    @Query("SELECT r FROM Round r "
        + "WHERE r.study.id = :studyId AND r.startDate <= :date AND r.endDate >= :date")
    Optional<Round> findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(
        Long studyId,
        LocalDate date);

    @Query("SELECT r FROM Round r "
        + "WHERE r.study.id = :studyId AND ("
        + " (r.startDate <= :currentDate AND r.endDate >= :currentDate) OR "
        + " (r.endDate < :currentDate AND r.idx = :lastIdx)"
        + ")")
    Optional<Round> findRoundByStudyIdAndBetweenStartDateAndEndDateOrIdx(Long studyId,
        Integer lastIdx, LocalDate currentDate);

    @Query("SELECT r FROM Round r "
        + "WHERE r.study.id = :studyId AND r.idx = :idx")
    Optional<Round> findRoundByStudyAndIdx(Long studyId, Integer idx);
}

