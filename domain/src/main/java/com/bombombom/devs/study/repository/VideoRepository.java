package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.Assignment;
import com.bombombom.devs.study.model.Video;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VideoRepository extends JpaRepository<Video, Long> {


    @Query("SELECT v FROM Video v "
        + "WHERE assignment IN :assignments AND v.uploader.id IN :memberIds ")
    List<Video> findAllByAssignmentInAndUploaderIdIn(List<Assignment> assignments,
        List<Long> memberIds);
}
