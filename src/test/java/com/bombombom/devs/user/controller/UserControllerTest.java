package com.bombombom.devs.user.controller;

import com.bombombom.devs.global.exception.GlobalExceptionHandler;
import com.bombombom.devs.user.controller.dto.SignupRequest;
import com.bombombom.devs.user.service.UserService;
import com.bombombom.devs.user.service.dto.SignupCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerTest {

    private final String USERNAME = "bombombom";
    private final String PASSWORD = "1234";

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupCommand))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("username이 빈 경우 회원가입이 실패한다.")
    @Test
    void signup_without_username_fail() throws Exception {
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("공백일 수 없습니다."));
    }

    @DisplayName("password가 빈 경우 회원가입이 실패한다.")
    @Test
    void signup_without_password_fail() throws Exception {
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("공백일 수 없습니다."));
    }

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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("255자를 넘을 수 없습니다."));
    }
}
