package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.UserStudy;
import com.bombombom.devs.user.model.User;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface UserStudyRepository extends JpaRepository<UserStudy, Long> {

    boolean existsByStudyIdAndUserId(Long studyId, Long userId);

    @Query("SELECT us FROM UserStudy us JOIN FETCH us.user "
        + "WHERE us.study.id = :studyId")
    List<UserStudy> findWithUserByStudyId(Long studyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT us FROM UserStudy us "
        + "WHERE us.study = :study AND us.user = :user")
    Optional<UserStudy> findByStudyAndUserForUpdate(Study study, User user);
}

