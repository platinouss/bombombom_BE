package com.bombombom.devs.book.naverapi;

import com.bombombom.devs.book.naverapi.dto.NaverBookApiRequest;
import com.bombombom.devs.book.naverapi.dto.NaverBookApiResponse;
import com.bombombom.devs.book.naverapi.exception.ExternalApiException;
import com.bombombom.devs.global.util.converter.MultiValueMapConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NaverClient {

    private final ObjectMapper objectMapper;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    @Value("${naver.url.search.book}")
    private String naverBookApiUrl;

    public NaverBookApiResponse requestBookApi(NaverBookApiRequest naverBookApiRequest,
        String apiUrl) {
        WebClient webClient = WebClient.builder().baseUrl(apiUrl).build();
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.queryParams(
                MultiValueMapConverter.convert(objectMapper, naverBookApiRequest)).build())
            .header("X-Naver-Client-Id", naverClientId)
            .header("X-Naver-Client-Secret", naverClientSecret)
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(),
                clientResponse -> Mono.just(
                    new ExternalApiException("NAVER Open API 요청에 실패했습니다."))
            )
            .bodyToMono(NaverBookApiResponse.class)
            .block();
    }

    NaverBookApiResponse searchBooks(NaverBookApiRequest naverBookApiRequest) {
        return requestBookApi(naverBookApiRequest, naverBookApiUrl);
    }
}
