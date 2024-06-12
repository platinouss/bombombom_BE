package com.bombombom.devs.user.service;

import com.bombombom.devs.user.UserRepository;
import com.bombombom.devs.user.exception.ExistUsernameException;
import com.bombombom.devs.user.service.dto.SignupCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
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
}
