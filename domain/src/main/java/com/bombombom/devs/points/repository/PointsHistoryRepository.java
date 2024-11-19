package com.bombombom.devs.points.repository;

import com.bombombom.devs.points.model.PointsHistory;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface PointsHistoryRepository extends JpaRepository<PointsHistory, Long> {

    @Query("SELECT ph FROM PointsHistory ph WHERE ph.user.id = :userId "
        + "ORDER BY ph.createdAt DESC "
        + "LIMIT 1")
    Optional<PointsHistory> findTopByUserOrderByCreatedAtDesc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ph FROM PointsHistory ph WHERE ph.user.id = :userId "
        + "ORDER BY ph.createdAt DESC "
        + "LIMIT 1")
    PointsHistory findTopByUserOrderByCreatedAtDescForUpdate(Long userId);
}
