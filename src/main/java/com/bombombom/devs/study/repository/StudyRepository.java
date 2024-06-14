package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.Study;
import java.awt.print.Pageable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {

}

