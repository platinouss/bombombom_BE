package com.bombombom.devs.external.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.bombombom.devs.core.exception.DuplicationException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.external.points.service.PointsService;
import com.bombombom.devs.external.user.service.dto.SignupCommand;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.user.model.Role;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String USERNAME = "bombombom";
    private static final String PASSWORD = "1234";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointsService pointsService;

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
        assertThatThrownBy(() -> userService.addUser(signupCommand))
            .isInstanceOf(DuplicationException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_USERNAME);
    }

    @DisplayName("username을 통해 유저를 조회할 수 있다.")
    @Test
    void can_retrieve_user_through_username() {
        /*
        Given
         */
        final Long points = 10000L;
        User user = User.builder()
            .id(1L)
            .username("username")
            .password("password")
            .introduce("introduce")
            .image("image")
            .baekjoon("baekjoon")
            .reliability(0)
            .role(Role.USER)
            .build();
        UserProfileResult userProfileResult = UserProfileResult.builder()
            .id(1L)
            .username("username")
            .image("image")
            .introduce("introduce")
            .baekjoonId("baekjoon")
            .reliability(0)
            .points(points)
            .role(Role.USER)
            .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(pointsService.getCurrentPoints(1L)).thenReturn(points);

        /*
        When & Then
         */
        assertThat(userService.findById(user.getId())).isEqualTo(userProfileResult);
    }
}
