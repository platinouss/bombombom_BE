package com.bombombom.devs.external.points.service;

import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.points.model.PointsHistory;
import com.bombombom.devs.points.repository.PointsHistoryRepository;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointsService {

    private final UserRepository userRepository;
    private final PointsHistoryRepository pointsHistoryRepository;

    @Transactional()
    public Long getCurrentPoints(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
            ErrorCode.USER_NOT_FOUND));
        PointsHistory currentPointsHistory = pointsHistoryRepository.findTopByUserOrderByCreatedAtDesc(
            userId).orElseGet(() -> pointsHistoryRepository.save(PointsHistory.init(user)));
        return currentPointsHistory.getTotal();
    }

    @Transactional
    public void updateUserPoints(User user, Long amount, String contents) {
        PointsHistory currentPointsHistory = pointsHistoryRepository.findTopByUserOrderByCreatedAtDescForUpdate(
            user.getId());
        PointsHistory updatedPointsHistory = PointsHistory.createUpdatePointsHistory(user,
            currentPointsHistory.getTotal(), amount, contents);
        pointsHistoryRepository.save(updatedPointsHistory);
    }
}
