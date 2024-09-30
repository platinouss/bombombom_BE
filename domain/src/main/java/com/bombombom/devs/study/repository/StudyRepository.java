package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.Study;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface StudyRepository extends JpaRepository<Study, Long> {

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

    @Query(value = "SELECT s FROM Study s "
        + "LEFT JOIN FETCH s.leader "
        + "LEFT JOIN FETCH TREAT(s as AlgorithmStudy).difficulties "
        + "LEFT JOIN FETCH TREAT(s as BookStudy).book ",
        countQuery = "SELECT COUNT(s) FROM Study s")
    Page<Study> findAllWithDifficultiesAndLeaderAndBook(Pageable pageable);


    @Query(value = "SELECT s FROM Study s "
        + "LEFT JOIN FETCH s.leader "
        + "LEFT JOIN FETCH TREAT(s as AlgorithmStudy).difficulties "
        + "LEFT JOIN FETCH TREAT(s as BookStudy).book "
        + "WHERE s.id = :id")
    Optional<Study> findWithDifficultiesAndLeaderAndBookById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Study s WHERE id = :id")
    Optional<Study> findByIdForUpdate(Long id);

}

