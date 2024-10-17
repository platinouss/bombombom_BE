package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.Study;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface StudyRepository extends JpaRepository<Study, Long> {


    @Query(value = "SELECT s FROM Study s "
        + "LEFT JOIN FETCH s.leader "
        + "LEFT JOIN FETCH TREAT(s as AlgorithmStudy).difficulties "
        + "LEFT JOIN FETCH TREAT(s as BookStudy).book "
        + "WHERE s.leader.id = :id")
    List<Study> findAllByLeader(Long id);

    @Query("select s from Study s "
        + "join fetch s.leader "
        + "where s.id = :id")
    Optional<Study> findWithLeaderById(Long id);

    @Query("SELECT s FROM Study s "
        + "LEFT JOIN FETCH TREAT(s as AlgorithmStudy).difficulties "
        + "WHERE s.id = :id")
    Optional<Study> findWithDifficultiesById(Long id);

    @Query("SELECT s FROM Study s "
        + "JOIN FETCH s.rounds "
        + "WHERE s.id = :id")
    Optional<Study> findWithRoundsById(Long id);

    @NonNull
    @Query(value = "SELECT s.id FROM Study s ",
        countQuery = "SELECT COUNT(s) FROM Study s")
    Page<Long> findIdsAll(@NonNull Pageable pageable);


    @Query(value = "SELECT s FROM Study s "
        + "LEFT JOIN FETCH s.leader "
        + "LEFT JOIN FETCH TREAT(s as AlgorithmStudy).difficulties "
        + "LEFT JOIN FETCH TREAT(s as BookStudy).book "
        + "WHERE s.id = :id")
    Optional<Study> findWithDifficultiesAndLeaderAndBookById(Long id);


    @Query(value = "SELECT s FROM Study s "
        + "LEFT JOIN FETCH s.leader "
        + "LEFT JOIN FETCH TREAT(s as AlgorithmStudy).difficulties "
        + "LEFT JOIN FETCH TREAT(s as BookStudy).book "
        + "WHERE s.id IN :ids")
    List<Study> findWithDifficultiesAndLeaderAndBookByIds(List<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Study s WHERE id = :id")
    Optional<Study> findByIdForUpdate(Long id);

}

