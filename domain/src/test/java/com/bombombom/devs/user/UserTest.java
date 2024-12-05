package com.bombombom.devs.user;


import static org.assertj.core.api.Java6Assertions.assertThat;

import com.bombombom.devs.points.model.PointsHistory;
import com.bombombom.devs.points.repository.PointsHistoryRepository;
import com.bombombom.devs.user.model.Role;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.List;
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
class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsHistoryRepository pointsHistoryRepository;

    @Test
    @DisplayName("회원가입 시 포인트 초기화 히스토리를 추가한다.")
    @Transactional
    void initialize_point_history_when_user_register() {
        /*
        Given
         */
        User testuser = User.builder()
            .username("testuser")
            .password("password")
            .role(Role.USER)
            .build();
        testuser.initPointHistory();

        /*
        When
         */
        userRepository.save(testuser);

        /*
        Then
         */
        User registeredUser = userRepository.findUserByUsername("testuser").get();
        List<PointsHistory> userPointsHistories = pointsHistoryRepository.findByUser(
            registeredUser);

        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getUsername()).isEqualTo("testuser");
        assertThat(userPointsHistories).hasSize(1);
        assertThat(userPointsHistories.get(0).getContents()).isEqualTo("초기화");
        assertThat(userPointsHistories.get(0).getAmount()).isEqualTo(0);
        assertThat(userPointsHistories.get(0).getTotal()).isEqualTo(0);
    }
}
