package com.bombombom.devs.external.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.core.util.SystemClock;
import com.bombombom.devs.external.config.TestUserDetailsServiceConfig;
import com.bombombom.devs.external.global.security.JwtUtils;
import com.bombombom.devs.external.user.controller.dto.SignupRequest;
import com.bombombom.devs.external.user.service.UserService;
import com.bombombom.devs.external.user.service.dto.SignupCommand;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.user.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
@Import({TestUserDetailsServiceConfig.class, JwtUtils.class, SystemClock.class})
class UserControllerTest {

    private static final String USERNAME = "bombombom";
    private static final String PASSWORD = "1234";

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser
    @DisplayName("회원가입을 할 수 있다.")
    @Test
    void signup_success() throws Exception {
        /*
        Given
         */
        SignupCommand signupCommand = SignupCommand.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .build();

        doNothing().when(userService).addUser(any(SignupCommand.class));

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/users/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupCommand))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser
    @DisplayName("username이 빈 경우 회원가입이 실패한다.")
    @Test
    void signup_without_username_fail() throws Exception {
        /*
        Given
         */
        SignupRequest signupRequest = SignupRequest.builder()
            .password(PASSWORD)
            .build();

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/users/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("공백일 수 없습니다."));
    }

    @WithMockUser
    @DisplayName("password가 빈 경우 회원가입이 실패한다.")
    @Test
    void signup_without_password_fail() throws Exception {
        /*
        Given
         */
        SignupRequest signupRequest = SignupRequest.builder()
            .username(USERNAME)
            .build();

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/users/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("공백일 수 없습니다."));
    }

    @WithMockUser
    @DisplayName("소개글이 255자를 넘을 경우 회원가입이 실패한다.")
    @Test
    void signup_when_introduce_exceed_255_characters_fail() throws Exception {
        /*
        Given
         */
        SignupRequest signupRequest = SignupRequest.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .introduce("봄".repeat(256))
            .build();

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/users/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("255자를 넘을 수 없습니다."));
    }

    /*
    HandlerMethodArgumentResolver 는 Component 일지라도 등록이 된다.
    다른 문제를 찾아야 함
     */
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "testUserDetailsService")
    @DisplayName("로그인한 유저는 자기 자신의 정보를 조회할 수 있다.")
    @Test
    void login_user_can_retrieve_own_information() throws Exception {
        /*
        Given
         */
        when(userService.findById(any())).thenReturn(UserProfileResult.builder()
            .id(1L)
            .username("testuser")
            .role(Role.USER)
            .image("image")
            .introduce("introduce")
            .reliability(0)
            .money(0)
            .build());

        /*
        When
         */
        ResultActions resultActions = this.mockMvc.perform(get("/api/v1/users/me"));

        /*
        Then
         */
        resultActions.andExpect(status().isOk());
    }
}
