package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.models.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query(value = "SELECT s FROM Study s LEFT JOIN FETCH s.leader LEFT JOIN FETCH TREAT(s as BookStudy).book",
        countQuery = "SELECT count(s) FROM Study s")
    Page<Study> findAllWithUserAndBook(Pageable pageable);
}

