package com.bombombom.devs.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.bombombom.devs.user.models.Role;
import com.bombombom.devs.user.models.User;
import com.bombombom.devs.user.repository.UserRepository;
import com.bombombom.devs.user.service.dto.UserProfileResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest2 {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("username을 통해 유저를 조회할 수 있다.")
    @Test
    void can_retrieve_user_through_username() {
        /*
        Given
         */
        User user = User.builder()
                .username("username")
                .password("password")
                .role(Role.USER)
                .introduce("introduce")
                .image("image")
                .reliability(0)
                .money(0)
                .build();
        userRepository.save(user);

        /*
        When
         */
        UserProfileResult result = userService.findByUsername(user.getUsername());

        /*
        Then
         */
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.username()).isEqualTo(user.getUsername());
        assertThat(result.role()).isEqualTo(user.getRole());
        assertThat(result.introduce()).isEqualTo(user.getIntroduce());
        assertThat(result.image()).isEqualTo(user.getImage());
        assertThat(result.reliability()).isEqualTo(user.getReliability());
        assertThat(result.money()).isEqualTo(user.getMoney());
    }
}
