package com.bombombom.devs.book.naverapi;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bombombom.devs.book.naverapi.exception.ExternalApiException;
import com.bombombom.devs.book.service.dto.NaverBookApiQuery;
import com.bombombom.devs.global.util.converter.MultiValueMapConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Import({NaverClient.class, MultiValueMapConverter.class})
public class NaverClientTest {

    @InjectMocks
    private NaverClient naverClient;

    @Spy
    private ObjectMapper objectMapper;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void mockMvcServerSetup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void mockMvcServerTerminate() throws IOException {
        mockWebServer.shutdown();
    }

    @DisplayName("Open API를 통해 서적 정보를 가져올 수 있다.")
    @Test
    void search_book_using_open_api() {
        /*
        Given
         */
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        String mockWebServerUrl = mockWebServer.url("/v1/search/book.json").toString();
        NaverBookApiQuery bookApiQuery = new NaverBookApiQuery("가상 면접 사례로 배우는");

        /*
        When & Then
         */
        assertThatCode(
            () -> naverClient.requestBookApi(bookApiQuery, mockWebServerUrl, objectMapper))
            .doesNotThrowAnyException();
    }

    @DisplayName("호출 한도 초과 등 응답으로 에러를 받은 경우 검색이 실패한다.")
    @Test
    void error_response_received_when_search_book_fail() {
        /*
        Given
         */
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));
        String mockWebServerUrl = mockWebServer.url("/v1/search/book.json").toString();
        NaverBookApiQuery bookApiQuery = new NaverBookApiQuery("가상 면접 사례로 배우는");

        /*
        When & Then
         */
        assertThrows(ExternalApiException.class, () -> naverClient.requestBookApi(bookApiQuery,
            mockWebServer.url(mockWebServerUrl).toString(), objectMapper));
    }
}


