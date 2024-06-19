package com.bombombom.devs.book.naverapi;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bombombom.devs.book.naverapi.dto.NaverBookApiRequest;
import com.bombombom.devs.book.naverapi.exception.ExternalApiException;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class NaverClientTest {

    private final NaverClient naverClient;

    private static MockWebServer mockWebServer;

    @Autowired
    public NaverClientTest(NaverClient naverClient) {
        this.naverClient = naverClient;
    }

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
        NaverBookApiRequest bookApiRequest = new NaverBookApiRequest("가상 면접 사례로 배우는");

        /*
        When & Then
         */
        assertThatCode(() -> naverClient.searchBooks(bookApiRequest))
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
        NaverBookApiRequest bookApiRequest = new NaverBookApiRequest("가상 면접 사례로 배우는");

        /*
        When & Then
         */
        assertThrows(ExternalApiException.class, () -> naverClient.requestBookApi(bookApiRequest,
            mockWebServer.url(mockWebServerUrl).toString()));
    }
}


