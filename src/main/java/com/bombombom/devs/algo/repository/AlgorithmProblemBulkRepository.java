package com.bombombom.devs.algo.repository;

import com.bombombom.devs.algo.models.AlgorithmProblem;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class AlgorithmProblemBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<AlgorithmProblem> problems) {
        String sql = "INSERT IGNORE INTO algorithm_problem (ref_id, tag, title, link, difficulty)"
            + "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, problems, problems.size(), (PreparedStatement ps, AlgorithmProblem problem) -> {
            ps.setInt(1, problem.getRefId());
            ps.setString(2, problem.getTag().name());
            ps.setString(3, problem.getTitle());
            ps.setString(4, problem.getLink());
            ps.setInt(5, problem.getDifficulty());
        });
    }
}
