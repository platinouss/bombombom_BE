package com.bombombom.devs.study.repository;

import com.bombombom.devs.study.models.AlgorithmProblemAssignment;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class AlgorithmProblemAssignmentBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<AlgorithmProblemAssignment> assignments) {
        String sql = "INSERT IGNORE INTO algorithm_problem_assignment (round_id, problem_id)"
            + "VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, assignments, assignments.size(),
            (PreparedStatement ps, AlgorithmProblemAssignment assignment) -> {
                ps.setLong(1, assignment.getRound().getId());
                ps.setLong(2, assignment.getProblem().getId());
            });
    }
}
