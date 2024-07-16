package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.BookStudy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookStudyRepository extends JpaRepository<BookStudy, Long> {

}
