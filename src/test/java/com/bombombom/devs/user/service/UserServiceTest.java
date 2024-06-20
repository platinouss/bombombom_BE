package com.bombombom.devs.user.service;

import com.bombombom.devs.user.exception.ExistUsernameException;
import com.bombombom.devs.user.models.Role;
import com.bombombom.devs.user.models.User;
import com.bombombom.devs.user.repository.UserRepository;
import com.bombombom.devs.user.service.dto.SignupCommand;
import com.bombombom.devs.user.service.dto.UserProfileResult;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final String USERNAME = "bombombom";
    private static final String PASSWORD = "1234";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("username이 존재하는 경우 회원가입이 실패한다.")
    @Test
    void signup_withExistingUsername_Fail() {
        /*
        Given
         */
        SignupCommand signupCommand = SignupCommand.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .build();

        doReturn(true).when(userRepository).existsByUsername(any(String.class));

        /*
        When & Then
         */
        assertThrows(ExistUsernameException.class, () -> userService.addUser(signupCommand));
    }

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
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        /*
        When
         */
        UserProfileResult result = userService.findById(user.getId());

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
