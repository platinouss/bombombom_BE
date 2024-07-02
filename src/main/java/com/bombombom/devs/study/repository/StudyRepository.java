package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.models.Study;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("select s from Study s "
        + "join fetch s.userStudies us "
        + "join fetch us.user "
        + "where s.id = :id")
    Optional<Study> findStudyWithUsersById(Long id);
    @Query(value = "SELECT s FROM Study s "
        + "LEFT JOIN FETCH s.leader "
        + "LEFT JOIN FETCH TREAT(s as BookStudy).book",
        countQuery = "SELECT count(s) FROM Study s")
    Page<Study> findAllWithUserAndBook(Pageable pageable);
}

