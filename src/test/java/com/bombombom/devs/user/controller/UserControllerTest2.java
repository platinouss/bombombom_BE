package com.bombombom.devs.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.user.controller.dto.UserProfileResponse;
import com.bombombom.devs.user.models.Role;
import com.bombombom.devs.user.models.User;
import com.bombombom.devs.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest2 {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "username")
    @DisplayName("로그인한 유저는 자기 자신의 정보를 조회할 수 있다.")
    void login_user_can_retrieve_own_information() throws Exception {
        /*
        Given
         */
        // Database Setting
        User user = User.builder()
            .id(1L)
            .username("username")
            .password(passwordEncoder.encode("password"))
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
        ResultActions resultActions = this.mockMvc.perform(get("/api/v1/users/me"));

        /*
        Then
         */
        String response = objectMapper.writeValueAsString(
            UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .image(user.getImage())
                .introduce(user.getIntroduce())
                .reliability(user.getReliability())
                .money(user.getMoney())
                .build());
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(response));
    }
}
