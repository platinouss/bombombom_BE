package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.models.UserStudy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStudyRepository extends JpaRepository<UserStudy, Long> {

    boolean existsByUserIdAndStudyId(Long userId, Long studyId);

}

