package com.bombombom.devs.study;


import static com.bombombom.devs.study.Constants.MAX_CAPACITY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.global.web.LoginUserArgumentResolver;
import com.bombombom.devs.study.controller.StudyController;
import com.bombombom.devs.study.controller.dto.request.JoinStudyRequest;
import com.bombombom.devs.study.controller.dto.response.StudyPageResponse;
import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.service.dto.result.StudyResult;
import com.bombombom.devs.user.models.Role;
import com.bombombom.devs.user.models.User;
import com.bombombom.devs.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StudyIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyController studyController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*
    TODO: 유저 인증 혹은 요청 유저의 데이터가 필요없는 테스트의 경우
     아래 BeforeEach로 User를 저장하는 것이 불 필요한 작업이 된다.
     StudyIntegrationTest | StudyIntegrationWithAuthenticationTest
     그렇다면 위처럼 2가지 클래스로 나누어야 할까? (특정 메소드만 DB세팅 하는 방법이 없음)
     */
    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(studyController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), new LoginUserArgumentResolver())
            .build();
        User user = User.builder()
            .id(1L)
            .username("testuser")
            .password(passwordEncoder.encode("password"))
            .role(Role.USER)
            .introduce("introduce")
            .image("image")
            .reliability(50)
            .money(10000)
            .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("스터디 목록을 offset기반 pagination을 통해 조회할 수 있다")
    void can_retrieve_study_list_through_offset_based_pagination()
        throws Exception {
        /*
        Given
         */
        Study study1 =
            AlgorithmStudy.builder()
                .reliabilityLimit(37)
                .introduce("안녕하세요")
                .name("스터디1")
                .startDate(LocalDate.of(2024, 06, 14))
                .penalty(5000)
                .weeks(5)
                .difficultyDp(12.4f)
                .difficultyDs(12f)
                .difficultyGraph(12.9f)
                .difficultyGap(5)
                .capacity(10)
                .difficultyGeometry(11f)
                .difficultyMath(11f)
                .difficultyString(13.5f)
                .problemCount(5)
                .build();

        Study study2 =
            BookStudy.builder()
                .reliabilityLimit(37)
                .capacity(10)
                .introduce("안녕하세요")
                .startDate(LocalDate.of(2024, 06, 14))
                .name("스터디1")
                .penalty(5000)
                .weeks(5)
                .bookId(1024L)
                .build();

        studyRepository.save(study1);
        studyRepository.save(study2);
        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            get("/api/v1/studies")
                .param("page", "1")
                .param("size", "1")
        );


        /*
        Then
         */
        List<StudyResponse> studies = new ArrayList<>();
        studies.add(StudyResponse.fromResult(StudyResult.fromEntity(study1)));

        StudyPageResponse studyPageResponse = StudyPageResponse.builder()
            .totalPages(2)
            .totalElements(2L)
            .pageNumber(1)
            .contents(studies)
            .build();
        String expectedResponse = objectMapper.writeValueAsString(studyPageResponse);
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("스터디 입장 조건에 맞는 유저는 입장할 수 있다.")
    @WithUserDetails(value = "testuser",
        userDetailsServiceBeanName = "appUserDetailsService",
        setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void can_join_study_if_user_meets_the_conditions() throws Exception {
        /*
         Given
         */
        Study study =
            BookStudy.builder()
                .reliabilityLimit(37)
                .capacity(10)
                .introduce("안녕하세요")
                .startDate(LocalDate.of(2024, 06, 14))
                .name("스터디")
                .penalty(1000)
                .weeks(5)
                .bookId(1024L)
                .build();
        studyRepository.save(study);
        JoinStudyRequest request = JoinStudyRequest.builder()
            .studyId(study.getId()).build();

        /*
         When
         */
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/studies/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        /*
        Then
         */
        resultActions.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인한 사용자는 알고리즘 스터디를 생성할 수 있다")
    void authenticated_user_can_create_algorithm_study() {

    }

    @Test
    @DisplayName("로그인하지 않은 사용자는 알고리즘 스터디를 생성할 수 없다")
    void unauthenticated_user_can_create_algorithm_study() {

    }

    @Test //-> 단위테스트로 이동
    @DisplayName("capcity가 1이상 " + MAX_CAPACITY + "이하인 스터디는 생성할 수 없다")
    void study_capacity_is_from_1_to_max_capcity() {

    }


}
