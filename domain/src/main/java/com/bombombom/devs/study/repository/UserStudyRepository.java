package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.UserStudy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserStudyRepository extends JpaRepository<UserStudy, Long> {

    boolean existsByStudyIdAndUserId(Long studyId, Long userId);

    @Query("SELECT us FROM UserStudy us JOIN FETCH us.user "
        + "WHERE us.study.id = :studyId")
    List<UserStudy> findWithUserByStudyId(Long studyId);
}

