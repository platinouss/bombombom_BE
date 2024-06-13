package com.bombombom.devs.study.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.study.controller.dto.response.AlgorithmStudyResponse;
import com.bombombom.devs.study.controller.dto.response.BookStudyResponse;
import com.bombombom.devs.study.controller.dto.response.StudyResponse;
import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.models.StudyType;
import com.bombombom.devs.study.service.StudyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = StudyController.class, properties = "spring.main.lazy-initialization=true")
class StudyControllerTest {


    @MockBean
    private StudyService studyService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("스터디 컨트롤러의 studyList 메소드는 Study 리스트의 JSON을 반환한다 ")
    void study_controller() throws Exception {

        /*
        Given
         */
        List<StudyResponse> serviceResponse = new ArrayList<>();

        StudyResponse studyResponse1 =
            AlgorithmStudyResponse.builder()
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

        StudyResponse studyResponse2 =
            BookStudyResponse.builder()
                .reliabilityLimit(37)
                .capacity(10)
                .introduce("안녕하세요")
                .startDate(LocalDate.of(2024, 06, 14))
                .name("스터디1")
                .penalty(5000)
                .weeks(5)
                .bookId(1024L)
                .build();

        serviceResponse.add(studyResponse1);
        serviceResponse.add(studyResponse2);

        when(studyService.readStudy(any(Pageable.class))).thenReturn(serviceResponse);

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            get("/api/v1/studies")
                .param("page", "1")
                .param("size", "10")
        );

        /*
        Then
         */
        String expectedResponse = objectMapper.writeValueAsString(serviceResponse);
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));

        System.out.println("expectedResponse = " + expectedResponse);

    }


}