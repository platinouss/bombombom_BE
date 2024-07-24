package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.Study;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    @Modifying
    @Query(
        "UPDATE Study s SET s.difficultyDp = s.difficultyDp+:variance where s.id = :studyId"
    )
    int increaseDifficultyDpById(Long studyId, Float variance);

    @Modifying
    @Query(
        "UPDATE Study s SET s.difficultyImpl = s.difficultyImpl+:variance where s.id = :studyId"
    )
    int increaseDifficultyImplementationById(Long studyId, Float variance);

    @Modifying
    @Query(
        "UPDATE Study s SET s.difficultyGraph = s.difficultyGraph+:variance where s.id = :studyId"
    )
    int increaseDifficultyGraphById(Long studyId, Float variance);

    @Modifying

    @Query(
        "UPDATE Study s SET s.difficultyDs = s.difficultyDs+:variance where s.id = :studyId"
    )
    int increaseDifficultyDataStructureById(Long studyId, Float variance);

    @Modifying
    @Query(
        "UPDATE Study s SET s.difficultyString = s.difficultyString+:variance where s.id = :studyId"
    )
    int increaseDifficultyStringById(Long studyId, Float variance);


    @Modifying
    @Query(
        "UPDATE Study s SET s.difficultyGeometry = s.difficultyGeometry+:variance where s.id = :studyId"
    )
    int increaseDifficultyGeometryById(Long studyId, Float variance);

    @Modifying
    @Query(
        "UPDATE Study s SET s.difficultyGreedy = s.difficultyGreedy+:variance where s.id = :studyId"
    )
    int increaseDifficultyGreedyById(Long studyId, Float variance);

    @Modifying
    @Query(
        "UPDATE Study s SET s.difficultyMath = s.difficultyMath+:variance where s.id = :studyId"
    )
    int increaseDifficultyMathById(Long studyId, Float variance);


}

