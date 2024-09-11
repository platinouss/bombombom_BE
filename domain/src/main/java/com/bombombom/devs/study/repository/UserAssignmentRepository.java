package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.model.Assignment;
import com.bombombom.devs.study.model.UserAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserAssignmentRepository extends JpaRepository<UserAssignment, Long> {


    @Query("SELECT ua FROM UserAssignment ua "
        + "WHERE assignment IN :assignments AND ua.user.id IN :memberIds")
    List<UserAssignment> findAllByAssignmentInAndUserIdIn(List<Assignment> assignments,
        List<Long> memberIds);
}
