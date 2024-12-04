package com.bombombom.devs.external.points.service;

import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.points.model.PointsHistory;
import com.bombombom.devs.points.repository.PointsHistoryRepository;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointsService {

    private final UserRepository userRepository;
    private final PointsHistoryRepository pointsHistoryRepository;

    @Transactional
    public Long getCurrentPoints(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
            ErrorCode.USER_NOT_FOUND));
        PointsHistory currentPointsHistory = pointsHistoryRepository.findTopByUserOrderByCreatedAtDesc(
            userId).orElseGet(() -> pointsHistoryRepository.save(PointsHistory.init(user)));
        return currentPointsHistory.getTotal();
    }

    @Transactional(readOnly = true)
    public List<PointsHistoryResult> getPointsHistory(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
            ErrorCode.USER_NOT_FOUND));
        List<PointsHistory> pointsHistories = pointsHistoryRepository.findByUser(user).stream()
            .filter(pointsHistory -> pointsHistory.getAmount() != 0).toList();
        return pointsHistories.stream().map(PointsHistoryResult::fromEntity).toList();
    }

    @Transactional
    public void updateUserPoints(User user, Long amount, String contents) {
        PointsHistory currentPointsHistory = pointsHistoryRepository.findTopByUserOrderByCreatedAtDescForUpdate(
            user.getId());
        if (amount < 0 && currentPointsHistory.getTotal() + amount < 0) {
            throw new BusinessRuleException(ErrorCode.NOT_ENOUGH_MONEY);
        }
        PointsHistory updatedPointsHistory = PointsHistory.createUpdatePointsHistory(user,
            currentPointsHistory.getTotal(), amount, contents);
        pointsHistoryRepository.save(updatedPointsHistory);
    }

    @Transactional
    public void payStudyDeposit(Study study, User user) {
        updateUserPoints(user, -study.calculateDeposit(), study.getName() + " 스터디 보증금");
    }
}
