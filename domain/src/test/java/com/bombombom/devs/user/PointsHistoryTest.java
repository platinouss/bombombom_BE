package com.bombombom.devs.user;

import static org.assertj.core.api.Java6Assertions.assertThat;

import com.bombombom.devs.points.model.PointsHistory;
import com.bombombom.devs.points.repository.PointsHistoryRepository;
import com.bombombom.devs.user.model.Role;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PointsHistoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsHistoryRepository pointsHistoryRepository;

    @BeforeEach
    void init() {
        User testuser = User.builder()
            .username("testuser")
            .password("password")
            .role(Role.USER)
            .build();
        testuser.initPointHistory();
        userRepository.save(testuser);

        PointsHistory currentPointsHistory = pointsHistoryRepository.findTopByUserOrderByCreatedAtDescForUpdate(
            testuser.getId());
        PointsHistory updatedPointsHistory = PointsHistory.createUpdatePointsHistory(testuser,
            currentPointsHistory.getTotal(), 10000L, "test용 10000 포인트 적립");
        pointsHistoryRepository.save(updatedPointsHistory);
    }

    @Test
    @Transactional
    @DisplayName("유저는 포인트를 지급받을 수 있다.")
    void user_can_award_points() {
        /*
         * Given
         */
        User testuser = userRepository.findUserByUsername("testuser").get();
        PointsHistory initPointsHistory = pointsHistoryRepository.findTopByUserOrderByCreatedAtDescForUpdate(
            testuser.getId());

        /*
         * When
         */
        PointsHistory updatedPointsHistory = PointsHistory.createUpdatePointsHistory(testuser,
            initPointsHistory.getTotal(), 10000L, "test용 10000 포인트 적립");
        pointsHistoryRepository.save(updatedPointsHistory);

        /*
         * Then
         */
        PointsHistory currentPointsHistory = pointsHistoryRepository.findTopByUserOrderByCreatedAtDescForUpdate(
            testuser.getId());

        assertThat(testuser).isNotNull();
        assertThat(currentPointsHistory.getAmount()).isEqualTo(10000L);
        assertThat(currentPointsHistory.getTotal()).isEqualTo(20000L);
        assertThat(currentPointsHistory.getContents()).isEqualTo("test용 10000 포인트 적립");
    }

    @Test
    @Transactional
    @DisplayName("유저는 자신이 가진 재화를 지불할 수 있다.")
    void user_can_pay_points() {
        /*
         * Given
         */
        User testuser = userRepository.findUserByUsername("testuser").get();
        PointsHistory initPointsHistory = pointsHistoryRepository.findTopByUserOrderByCreatedAtDescForUpdate(
            testuser.getId());

        /*
         * When
         */
        PointsHistory updatedPointsHistory = PointsHistory.createUpdatePointsHistory(testuser,
            initPointsHistory.getTotal(), -5000L, "test용 5000 포인트 차감");
        pointsHistoryRepository.save(updatedPointsHistory);

        /*
         * Then
         */
        PointsHistory currentPointsHistory = pointsHistoryRepository.findTopByUserOrderByCreatedAtDescForUpdate(
            testuser.getId());

        assertThat(testuser).isNotNull();
        assertThat(currentPointsHistory.getAmount()).isEqualTo(-5000L);
        assertThat(currentPointsHistory.getTotal()).isEqualTo(5000L);
        assertThat(currentPointsHistory.getContents()).isEqualTo("test용 5000 포인트 차감");
    }
}
