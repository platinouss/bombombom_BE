package com.bombombom.devs.external.algo;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.ExternalApiApplication;
import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.external.algo.controller.dto.request.FeedbackAlgorithmProblemRequest;
import com.bombombom.devs.external.config.ElasticsearchTestConfig;
import com.bombombom.devs.external.study.controller.StudyController;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.StudyStatus;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.user.model.Role;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = ExternalApiApplication.class)
@Import(ElasticsearchTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AlgoIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AlgorithmProblemRepository problemRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyController studyController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Clock clock;


    @Nested
    @DisplayName("인증이 필요한 테스트")
    @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    class TestWithAuthentication {

        private AlgorithmProblem problem;
        private User leader;
        private AlgorithmStudy study;

        @BeforeEach
        public void init() {

            leader = User.builder()
                .username("leader")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .introduce("introduce")
                .image("image")
                .reliability(50)
                .money(10000)
                .build();
            userRepository.save(leader);

            problem = AlgorithmProblem.builder()
                .title("히스토그램에서 가장큰 직사각형")
                .refId(1023)
                .tag(AlgoTag.DATA_STRUCTURES)
                .difficulty(15)
                .build();

            problemRepository.save(problem);

            study =
                AlgorithmStudy.builder()
                    .reliabilityLimit(37)
                    .introduce("안녕하세요")
                    .name("스터디1")
                    .startDate(LocalDate.of(2024, 06, 14))
                    .penalty(5000)
                    .weeks(5)
                    .state(StudyStatus.READY)
                    .headCount(0)
                    .leader(leader)
                    .difficultyDp(12.4f)
                    .difficultyDs(12f)
                    .difficultyGraph(12.9f)
                    .difficultyGap(5)
                    .capacity(10)
                    .difficultyGeometry(11f)
                    .difficultyMath(11f)
                    .difficultyString(13.5f)
                    .problemCount(5)
                    .startDate(clock.today())
                    .build();
            study.admit(leader);
            study.createRounds();
            study.getRounds().getFirst().assignProblem(problem);
            studyRepository.save(study);

        }

        @Test
        @DisplayName("알고리즘 문제에 대한 피드백을 줄 수 있다.")
        @WithUserDetails(value = "leader",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void can_feedback() throws Exception {
            /*
             Given
             */
            FeedbackAlgorithmProblemRequest feedback = FeedbackAlgorithmProblemRequest.builder()
                .studyId(study.getId())
                .problemId(problem.getId())
                .again(true)
                .difficulty(4)
                .build();

            /*
             When
             */
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/algo/feedback")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(feedback))
            );

            /*
            Then
             */
            resultActions.andDo(print())
                .andExpect(status().isOk());

        }

    }


}
